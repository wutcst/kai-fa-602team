package cn.edu.whut.sept.zuul.ui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class GameApp extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader loader = new FXMLLoader(
                Objects.requireNonNull(getClass().getResource("/fxml/main.fxml")));
        Parent root = loader.load();
        GameController controller = loader.getController();
        controller.init();

        Scene scene = new Scene(root, 960, 640);
        scene.getStylesheets().add(
                Objects.requireNonNull(getClass().getResource("/css/game.css")).toExternalForm());

        stage.setTitle("祖尔世界 - 校园迷宫");
        stage.setScene(scene);
        stage.setMinWidth(800);
        stage.setMinHeight(520);
        stage.show();
    }
}
