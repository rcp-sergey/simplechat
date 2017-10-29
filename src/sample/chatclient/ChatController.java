package sample.chatclient;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.util.Duration;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class ChatController implements Initializable {
    @FXML
    private TextArea messageArea;
    @FXML
    private TextField messageInput;
    @FXML
    private GridPane authPane;
    @FXML
    private BorderPane chatPane;
    @FXML
    Button sendButton;
    @FXML
    private ListView onlineList;
    private Connection conn;

    public void sendMessage() {
        String message = messageInput.getText();
        if (!message.equals("")) {
                conn.sendMessage(message);
                messageInput.clear();
        }
    }

    // starts message receiving thread
    public void startReceive() {
        Thread receiveThread = new Thread( () -> {
            /*String msg;*/
            while (true) {
                final String msg;
                msg = conn.receiveMessage();
                if (msg != null && !msg.equals("")) {
                    if (msg.startsWith("/currentonlinelist")) {
                        Platform.runLater( () -> {
                            updateOnlineList(msg);
                        });
                    } else {
                        Platform.runLater( () -> {
                            messageArea.appendText(msg + "\n");
                        });
                    }
                }
            }
        });
        receiveThread.setDaemon(true);
        receiveThread.start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conn = Connection.getInstance();

        String filename = location.getFile().substring(location.getFile().lastIndexOf('/') + 1, location.getFile().length());
        if (filename.equals("auth.fxml")) {
            fadeTrans(authPane);
        } else if (filename.equals("chat.fxml")) {
            fadeTrans(chatPane);
        }
        startReceive();
        setListViewListener();
    }

    // listens for click on elements of ListView and adds (or replaces) prefix to the beginning of message input if it's necessary
    private void setListViewListener() {
        onlineList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if(onlineList.getSelectionModel().getSelectedItem() != null && !onlineList.getSelectionModel().getSelectedItem().equals("ONLINE") && !onlineList.getSelectionModel().getSelectedItem().toString().contains(" (Я)")) {
                    Object[] onlineListArray = onlineList.getItems().toArray();
                    String selectedNick = onlineList.getSelectionModel().getSelectedItem().toString();
                    String currentInput = messageInput.getText();
                    for (Object o: onlineListArray) {
                        if (currentInput.startsWith("/w " + o + " ")) {
                            messageInput.setText("/w " + selectedNick + " " + currentInput.substring(4 + ((String)o).length(), currentInput.length()));
                            return;
                        } else if (currentInput.startsWith("/w " + o + " ")) return;
                    }
                    messageInput.setText("/w " + selectedNick + " " + currentInput);
                }
            }
        });
    }
    // updates GUI's list of online users
    // TODO replace "online" entry with label
    public void updateOnlineList(String list) {
        String[] elements = list.split(" ");
        elements[0] = "ONLINE";
        for (int i = 0; i < elements.length; i++) if (elements[i].equals(conn.getCurrentNick())) elements[i] = elements[i] + " (Я)";
        onlineList.getItems().clear();
        onlineList.refresh();
        onlineList.getItems().addAll(elements);
        onlineList.refresh();
    }

    // Scene change animation settings
    public void fadeTrans(Node e) {
        FadeTransition x = new FadeTransition(new Duration(2000), e);
        x.setFromValue(0);
        x.setToValue(100);
        x.setCycleCount(1);
        x.setInterpolator(Interpolator.LINEAR);
        x.play();
    }
}
