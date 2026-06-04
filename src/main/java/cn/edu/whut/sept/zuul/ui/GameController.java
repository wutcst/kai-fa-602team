package cn.edu.whut.sept.zuul.ui;

import cn.edu.whut.sept.zuul.command.GameCommands;
import cn.edu.whut.sept.zuul.engine.CommandResult;
import cn.edu.whut.sept.zuul.engine.GameEngine;
import cn.edu.whut.sept.zuul.model.Item;
import cn.edu.whut.sept.zuul.model.Player;
import cn.edu.whut.sept.zuul.model.Room;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import cn.edu.whut.sept.zuul.model.World;

public class GameController
{
    @FXML private Label playerNameLabel;
    @FXML private TextArea roomDescriptionArea;
    @FXML private TextArea logArea;
    @FXML private FlowPane directionPane;
    @FXML private ListView<String> roomItemsList;
    @FXML private ListView<String> inventoryList;
    @FXML private ProgressBar weightBar;
    @FXML private Label weightLabel;
    @FXML private TextField commandField;

    private GameEngine engine;

    public void init()
    {
        engine = new GameEngine();
        playerNameLabel.setText("玩家: " + engine.getPlayer().getName());
        appendLog(engine.getWelcomeMessage());
        refreshView();
    }

    @FXML
    private void onExecuteCommand()
    {
        String input = commandField.getText();
        if (input == null || input.isBlank()) {
            return;
        }
        runCommand(input.trim());
        commandField.clear();
    }

    @FXML
    private void onHelp()
    {
        runCommand(GameCommands.HELP);
    }

    @FXML
    private void onLook()
    {
        runCommand(GameCommands.LOOK);
    }

    @FXML
    private void onBack()
    {
        runCommand(GameCommands.BACK);
    }

    @FXML
    private void onItems()
    {
        runCommand(GameCommands.ITEMS);
    }

    @FXML
    private void onEatCookie()
    {
        runCommand(GameCommands.EAT + " " + GameCommands.EAT_COOKIE_ARG);
    }

    @FXML
    private void onTakeSelected()
    {
        String selected = roomItemsList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            appendLog("请先在房间物品列表中选择一项。");
            return;
        }
        String itemName = extractItemName(selected);
        runCommand(GameCommands.TAKE + " " + itemName);
    }

    @FXML
    private void onDropSelected()
    {
        String selected = inventoryList.getSelectionModel().getSelectedItem();
        if (selected == null) {
            appendLog("请先在背包列表中选择一项。");
            return;
        }
        String itemName = extractItemName(selected);
        runCommand(GameCommands.DROP + " " + itemName);
    }

    @FXML
    private void onDropAll()
    {
        runCommand(GameCommands.DROP);
    }

    @FXML
    private void onSave()
    {
        runCommand(GameCommands.SAVE + " " + GameCommands.DEFAULT_SAVE_SLOT);
    }

    @FXML
    private void onLoad()
    {
        runCommand(GameCommands.LOAD + " " + GameCommands.DEFAULT_SAVE_SLOT);
    }

    @FXML
    private void onQuit()
    {
        CommandResult result = engine.quit();
        appendLog(result.getMessageText());
        Platform.exit();
    }

    private void runCommand(String commandLine)
    {
        appendLog("> " + commandLine);
        CommandResult result = engine.processCommandLine(commandLine);
        appendLog(result.getMessageText());
        if (result.isStateChanged()) {
            refreshView();
        }
        if (result.isFinished()) {
            Platform.runLater(() -> {
                Stage stage = (Stage) commandField.getScene().getWindow();
                stage.close();
            });
        }
    }

    private void refreshView()
    {
        Player player = engine.getPlayer();
        Room room = player.getCurrentRoom();

        roomDescriptionArea.setText(room.getLongDescription());
        rebuildDirectionButtons(room.getExitDirections());

        roomItemsList.getItems().clear();
        for (Item item : room.getItems()) {
            roomItemsList.getItems().add(item.getDisplayLine());
        }

        inventoryList.getItems().clear();
        for (Item item : player.getInventory().getItems()) {
            inventoryList.getItems().add(item.getDisplayLine());
        }

        double max = player.getInventory().getMaxWeight();
        double current = player.getInventory().getTotalWeight();
        weightBar.setProgress(max <= 0 ? 0 : Math.min(1.0, current / max));
        weightLabel.setText(String.format("负重: %.1f / %.1f 千克", current, max));
        if (player.hasEatenMagicCookie()) {
            weightLabel.setText(weightLabel.getText() + "  [已食用魔法饼干]");
        }
    }

    /** 方向按钮固定显示顺序：东、西、南、北 */
    private static final List<String> DIRECTION_DISPLAY_ORDER = Arrays.asList(
            World.DIR_EAST, World.DIR_WEST, World.DIR_SOUTH, World.DIR_NORTH);

    private void rebuildDirectionButtons(Set<String> directions)
    {
        directionPane.getChildren().clear();
        boolean any = false;
        for (String dir : DIRECTION_DISPLAY_ORDER) {
            if (!directions.contains(dir)) {
                continue;
            }
            any = true;
            Button btn = new Button(dir);
            btn.getStyleClass().add("direction-button");
            btn.setOnAction(e -> runCommand(GameCommands.GO + " " + dir));
            directionPane.getChildren().add(btn);
        }
        if (!any) {
            Label none = new Label("无出口");
            none.getStyleClass().add("muted-label");
            directionPane.getChildren().add(none);
        }
    }

    private void appendLog(String text)
    {
        if (text == null || text.isBlank()) {
            return;
        }
        logArea.appendText(text + "\n");
        logArea.setScrollTop(Double.MAX_VALUE);
    }

    private String extractItemName(String displayLine)
    {
        int idx = displayLine.indexOf(" (");
        return idx > 0 ? displayLine.substring(0, idx) : displayLine;
    }
}
