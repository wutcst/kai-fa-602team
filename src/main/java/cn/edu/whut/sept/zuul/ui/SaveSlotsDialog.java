package cn.edu.whut.sept.zuul.ui;

import cn.edu.whut.sept.zuul.engine.CommandResult;
import cn.edu.whut.sept.zuul.engine.GameEngine;
import cn.edu.whut.sept.zuul.model.Room;
import cn.edu.whut.sept.zuul.persistence.PersistenceException;
import cn.edu.whut.sept.zuul.persistence.SaveSlotSummary;
import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.util.Callback;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * 多存档管理对话框：数据库操作在后台线程执行，避免阻塞 UI。
 */
public final class SaveSlotsDialog
{
    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    private SaveSlotsDialog()
    {
    }

    public static void showSaveDialog(Window owner, GameEngine engine, Consumer<CommandResult> onResult)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.initOwner(owner);
        dialog.setTitle("保存游戏");
        dialog.setHeaderText("为当前进度命名存档");
        dialog.setContentText("存档名称:");

        Optional<String> input = dialog.showAndWait();
        if (input.isEmpty() || input.get().isBlank()) {
            return;
        }
        String slotName = input.get().trim();

        Optional<String> validationError = engine.validateSlotName(slotName);
        if (validationError.isPresent()) {
            showInfo(owner, validationError.get());
            return;
        }

        Task<Boolean> checkTask = new Task<>() {
            @Override
            protected Boolean call() throws Exception
            {
                return engine.saveExists(slotName);
            }
        };
        checkTask.setOnSucceeded(event -> {
            boolean exists = Boolean.TRUE.equals(checkTask.getValue());
            if (exists && !confirmOverwrite(owner, slotName)) {
                return;
            }
            runWithProgress(owner, "正在保存...", () -> engine.saveGame(slotName),
                    onResult, e -> showInfo(owner, "保存失败: " + e.getMessage()));
        });
        checkTask.setOnFailed(event -> {
            Throwable error = checkTask.getException();
            String message = error != null && error.getCause() != null
                    ? error.getCause().getMessage()
                    : (error != null ? error.getMessage() : "未知错误");
            showInfo(owner, "检查存档失败: " + message);
        });
        new Thread(checkTask, "check-save-exists").start();
    }

    public static void showLoadDialog(Window owner, GameEngine engine, Consumer<CommandResult> onResult)
    {
        pickSaveAsync(owner, engine, "读取存档", "请选择要读取的存档", selected -> {
            if (selected == null) {
                return;
            }
            runWithProgress(owner, "正在读取...", () -> engine.loadGame(selected.getSlotId()),
                    onResult, e -> showInfo(owner, "读取失败: " + e.getMessage()));
        });
    }

    public static void showDeleteDialog(Window owner, GameEngine engine, Consumer<CommandResult> onResult)
    {
        pickSaveAsync(owner, engine, "删除存档", "请选择要删除的存档", selected -> {
            if (selected == null) {
                return;
            }
            if (!confirmDelete(owner, selected.getSlotId())) {
                return;
            }
            runWithProgress(owner, "正在删除...", () -> engine.deleteGame(selected.getSlotId()),
                    onResult, e -> showInfo(owner, "删除失败: " + e.getMessage()));
        });
    }

    private static void pickSaveAsync(Window owner, GameEngine engine, String title, String header,
                                      Consumer<SaveSlotSummary> onSelected)
    {
        Dialog<SaveSlotSummary> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.setTitle(title);
        dialog.setHeaderText(header);

        ListView<SaveSlotSummary> listView = new ListView<>();
        listView.setPrefHeight(220);
        listView.setPrefWidth(420);
        listView.setPlaceholder(new Label("加载中..."));

        Map<String, String> roomNames = buildRoomNameMap(engine);
        listView.setCellFactory(summaryCellFactory(roomNames));

        VBox content = new VBox(8, new Label("已有存档:"), listView);
        content.setPadding(new Insets(10));
        dialog.getDialogPane().setContent(content);

        ButtonType confirmType = new ButtonType("确定", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(confirmType, ButtonType.CANCEL);
        Button confirmButton = (Button) dialog.getDialogPane().lookupButton(confirmType);
        if (confirmButton != null) {
            confirmButton.setDisable(true);
        }

        dialog.setResultConverter(button -> {
            if (button == confirmType) {
                return listView.getSelectionModel().getSelectedItem();
            }
            return null;
        });

        Task<List<SaveSlotSummary>> loadTask = new Task<>() {
            @Override
            protected List<SaveSlotSummary> call() throws Exception
            {
                return engine.listSaveSummaries();
            }
        };
        loadTask.setOnSucceeded(event -> {
            List<SaveSlotSummary> saves = loadTask.getValue();
            if (saves.isEmpty()) {
                dialog.close();
                showInfo(owner, "暂无存档，请先保存游戏。");
                return;
            }
            listView.getItems().setAll(saves);
            listView.getSelectionModel().selectFirst();
            if (confirmButton != null) {
                confirmButton.setDisable(false);
            }
        });
        loadTask.setOnFailed(event -> {
            dialog.close();
            Throwable error = loadTask.getException();
            String message = error != null ? error.getMessage() : "未知错误";
            if (error != null && error.getCause() != null) {
                message = error.getCause().getMessage();
            }
            showInfo(owner, "读取存档列表失败: " + message);
        });

        new Thread(loadTask, "load-save-list").start();
        Optional<SaveSlotSummary> selected = dialog.showAndWait();
        onSelected.accept(selected.orElse(null));
    }

    private static <T> void runWithProgress(Window owner, String message, Supplier<T> work,
                                            Consumer<T> onSuccess, Consumer<Throwable> onError)
    {
        Dialog<Void> progressDialog = createProgressDialog(owner, message);
        progressDialog.show();

        Task<T> task = new Task<>() {
            @Override
            protected T call()
            {
                return work.get();
            }
        };
        task.setOnSucceeded(event -> {
            progressDialog.close();
            onSuccess.accept(task.getValue());
        });
        task.setOnFailed(event -> {
            progressDialog.close();
            onError.accept(task.getException());
        });
        new Thread(task, "save-operation").start();
    }

    private static Dialog<Void> createProgressDialog(Window owner, String message)
    {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initOwner(owner);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setTitle("请稍候");
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CANCEL);
        dialog.getDialogPane().lookupButton(ButtonType.CANCEL).setVisible(false);

        ProgressIndicator indicator = new ProgressIndicator();
        Label label = new Label(message);
        VBox box = new VBox(12, indicator, label);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(20));
        dialog.getDialogPane().setContent(box);
        return dialog;
    }

    private static Map<String, String> buildRoomNameMap(GameEngine engine)
    {
        Map<String, String> roomNames = new HashMap<>();
        for (Room room : engine.getWorld().getRooms()) {
            roomNames.put(room.getId(), room.getDescription());
        }
        return roomNames;
    }

    private static Callback<ListView<SaveSlotSummary>, ListCell<SaveSlotSummary>> summaryCellFactory(
            Map<String, String> roomNames)
    {
        return listView -> new ListCell<>() {
            @Override
            protected void updateItem(SaveSlotSummary item, boolean empty)
            {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    return;
                }
                String roomName = roomNames.getOrDefault(item.getCurrentRoomId(), item.getCurrentRoomId());
                String time = item.getSavedAt() != null
                        ? item.getSavedAt().format(TIME_FORMAT)
                        : "未知时间";
                setText(String.format("%s | 玩家:%s | %s | %s",
                        item.getSlotId(), item.getPlayerName(), roomName, time));
            }
        };
    }

    private static boolean confirmOverwrite(Window owner, String slotName)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(owner);
        alert.setTitle("覆盖存档");
        alert.setHeaderText("存档「" + slotName + "」已存在");
        alert.setContentText("是否覆盖该存档？");
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private static boolean confirmDelete(Window owner, String slotName)
    {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(owner);
        alert.setTitle("删除存档");
        alert.setHeaderText("确认删除存档「" + slotName + "」？");
        alert.setContentText("此操作不可恢复。");
        Button deleteButton = (Button) alert.getDialogPane().lookupButton(ButtonType.OK);
        if (deleteButton != null) {
            deleteButton.setText("删除");
        }
        return alert.showAndWait().filter(ButtonType.OK::equals).isPresent();
    }

    private static void showInfo(Window owner, String message)
    {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.initOwner(owner);
        alert.setTitle("提示");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
