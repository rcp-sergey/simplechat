package sample.chatclient;

import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
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
            String msg;
            while (true) {
                msg = null;
                msg = conn.receiveMessage();
                if (msg != null && !msg.equals("")) {
                    if (msg.startsWith("/currentonlinelist")) updateOnlineList(msg);
                    else {
                        messageArea.appendText(msg + "\n");
                        System.out.println(msg);
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

    // listens for click on elements of ListView and starts private message input
    // TODO add prefix without deleting already entered message
    private void setListViewListener() {
        onlineList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if(onlineList.getSelectionModel().getSelectedItem() != null && !onlineList.getSelectionModel().getSelectedItem().equals("ONLINE NOW") && !onlineList.getSelectionModel().getSelectedItem().equals(Connection.getInstance().getCurrentNick()))
                    messageInput.setText("/w " + onlineList.getSelectionModel().getSelectedItem()+" ");
            }
        });
    }
    // updates GUI's list of online users
    public void updateOnlineList(String list) {
        String[] elements = list.split(" ");
        elements[0] = "ONLINE NOW";
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
