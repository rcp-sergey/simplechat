package sample.chatclient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

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
        systemMessage.setText("");
        String response = conn.auth(loginField.getText(), passField.getText());
        if (response.equals("/success")) {
            Client.changeScene("chat.fxml");
        } else if (response.equals("/reconnect")) {
            //
            authPane.setDisable(true);
            StringBuffer alert = new StringBuffer("Переподключение");
            for (int i = 0; i < 10; i ++) {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                systemMessage.setText(alert.append(".").toString());
            }
            authPane.setDisable(false);
            logIn();
        } else systemMessage.setText(response);
    }
}
