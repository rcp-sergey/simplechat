package sample.chatclient;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

import java.util.Timer;
import java.util.TimerTask;

public class AuthController {

    @FXML
    GridPane authPane;
    @FXML
    private TextField loginField;
    @FXML
    private PasswordField passField;
    @FXML
    private Button logInButton;
    @FXML
    private Label systemMessage;
    private Connection conn = Connection.getInstance();

    public void logIn() {
        // TODO disable elements during auth
        String login = loginField.getText();
        String pass = passField.getText();
        final String[] response = new String[1];
        Timer authTimer = new Timer();
        TimerTask authTask = new TimerTask() {
            @Override
            public void run() {
                boolean haveResponce = false;
                while (!haveResponce) {
                    response[0] = conn.auth(login, pass);
                    if (response[0].equals("/reconnect"))
                        Platform.runLater(() -> {
                            systemMessage.setText("Переподключение");
                        });
                    else if (response[0].equals("/success")) {
                        Platform.runLater(() -> {
                            systemMessage.setText("Вход выполнен");
                            Client.changeScene("chat.fxml");
                        });
                        haveResponce = true;
                        authTimer.cancel();
                    } else {
                        Platform.runLater(() -> {
                            systemMessage.setText("Вход выполнен");
                            systemMessage.setText(response[0]);
                        });
                        haveResponce = true;
                        authTimer.cancel();
                    }
                }
            }
        };
        authTimer.schedule(authTask, 0, 3000);
/*        *//*systemMessage.setText("");*//*
        *//*String response = conn.auth(loginField.getText(), passField.getText());*//*
        if (response[0].equals("/success")) {
            Client.changeScene("chat.fxml");
        } else if (response[0].equals("/reconnect")) {
            //
            loginField.setDisable(true);
            passField.setDisable(true);
            logInButton.setDisable(true);
            StringBuffer alert = new StringBuffer();
            alert.append("Переподключение");
            for (int i = 0; i < 10; i ++) {
                systemMessage.setText(alert.append(". ").toString());

                }
            loginField.setDisable(false);
            passField.setDisable(false);
            logInButton.setDisable(false);
            logIn();
        } else systemMessage.setText(response[0]);*/
    }
}
