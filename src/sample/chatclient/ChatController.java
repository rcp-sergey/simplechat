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
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
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
    private ListView onlineList;
    private DataInputStream in;
    private DataOutputStream out;

    public void sendMessage() {
        String message = messageInput.getText();
        if (!message.equals("")) {
            try {
                out.writeUTF(message);
                out.flush();
                messageInput.clear();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void startReceive() {
        new Thread( () -> {
                while (true) {
                    String msg = "";
                    msg = Connection.getInstance().receiveMessage();
                    if (!msg.equals("")) {
                        if (msg.startsWith("/currentonlinelist")) updateOnlineList(msg);
                        else {
                            messageArea.appendText(msg + "\n");
                            System.out.println(msg);
                        }
                    }
                }
        }).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        in = Connection.getIn();
        out = Connection.getOut();
        String filename = location.getFile().substring(location.getFile().lastIndexOf('/') + 1, location.getFile().length());
        if (filename.equals("auth.fxml")) {
            fadeTrans(authPane);
        } else if (filename.equals("chat.fxml")) {
            fadeTrans(chatPane);
        }
        startReceive();
        setListViewListener();
    }

    public void setListViewListener() {
        onlineList.setOnMouseClicked(new EventHandler<MouseEvent>() {

            @Override
            public void handle(MouseEvent event) {
                if(onlineList.getSelectionModel().getSelectedItem() != null && !onlineList.getSelectionModel().getSelectedItem().equals("ONLINE NOW") && !onlineList.getSelectionModel().getSelectedItem().equals(Connection.getInstance().getCurrentNick()))
                    messageInput.setText("/w " + onlineList.getSelectionModel().getSelectedItem()+" ");
            }
        });
    }

    public void updateOnlineList(String list) {
        System.out.println("list" + list);
        String[] elements = list.split(" ");
        elements[0] = "ONLINE NOW";
        onlineList.getItems().clear();
        onlineList.refresh();
        onlineList.getItems().addAll(elements);
        onlineList.refresh();
    }

    public void fadeTrans(Node e) {
        FadeTransition x = new FadeTransition(new Duration(2000), e);
        x.setFromValue(0);
        x.setToValue(100);
        x.setCycleCount(1);
        x.setInterpolator(Interpolator.LINEAR);
        x.play();
    }
}
