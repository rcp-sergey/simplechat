package sample.chatclient;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;


public class Client extends Application{
        static Stage stage;
        private static final int WIDTH = 450;
        private static final int HEIGHT = 450;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        Parent root = FXMLLoader.load(Client.class.getResource("auth.fxml"));
        primaryStage.setTitle("simpleChat");
        primaryStage.setResizable(true);
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void changeScene(String sceneName) {
        try {
            Parent root = FXMLLoader.load(Client.class.getResource(sceneName));
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                public void handle(WindowEvent we) {
                    Connection.getInstance().sendMessage("/end");
                    Platform.exit();
                }
            });
            stage.setTitle("simpleChat - " + Connection.getInstance().getCurrentNick());
            stage.setScene(new Scene(root, WIDTH, HEIGHT));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
