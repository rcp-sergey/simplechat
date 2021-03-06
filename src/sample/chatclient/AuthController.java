package sample.chatclient;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class AuthController {

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
        if (response.equals("success")) {
            Client.changeScene("chat.fxml");
        } else systemMessage.setText(response);
    }
}
