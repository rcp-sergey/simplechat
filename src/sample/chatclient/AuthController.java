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
    private final int MAX_ATTEMPTS = 10;

    public void logIn() {
        // TODO disable elements during auth

        String login = loginField.getText();
        String pass = passField.getText();
        if (loginField.getText().equals("") || passField.getText().equals("")) {
            systemMessage.setText("Введите учетные данные");
            return;
        }
        logInButton.setDisable(true);
        final String[] response = new String[1];
        Timer authTimer = new Timer();
        TimerTask authTask = new TimerTask() {
            @Override
            public void run() {
                int attempts = 0;
                boolean haveResponse = false;
                while (!haveResponse) {
                    response[0] = conn.auth(login, pass);
                    if (response[0].equals("/reconnect")) {
                        if (attempts < MAX_ATTEMPTS) {
                            final int attemptsToShow = attempts + 1;
                            Platform.runLater(() -> {
                                systemMessage.setText("Переподключение: попытка " + attemptsToShow);
                            });
                            attempts++;
                        } else {
                            Platform.runLater(() -> {
                                systemMessage.setText(conn.getNetState());
                                logInButton.setDisable(false);
                            });
                            haveResponse = true;
                            authTimer.cancel();
                        }
                    }
                    else if (response[0].equals("/success")) {
                        Platform.runLater(() -> {
                            systemMessage.setText("Вход выполнен");
                            Client.changeScene("chat.fxml");
                            logInButton.setDisable(false);
                        });
                        haveResponse = true;
                        authTimer.cancel();
                    } else {
                        Platform.runLater(() -> {
                            systemMessage.setText(response[0]);
                            logInButton.setDisable(false);
                        });
                        haveResponse = true;
                        authTimer.cancel();
                    }
                }
            }
        };
        authTimer.schedule(authTask, 0, 3000);
    }
}
