package sample.chatclient;

import javafx.application.Platform;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Timer;
import java.util.TimerTask;

//Singleton class that establishes and maintains clients connection to chat server
public class Connection {
    private static final String SERVER_ADDR = "localhost";
    private static final int SERVER_PORT = 4000;
    private Socket socket;
    private static DataInputStream in;
    private static DataOutputStream out;
    private String currentNick;
    private boolean wasConnected;
    private String netState;

    public String getCurrentNick() {
        return currentNick;
    }

    private static Connection ourInstance = new Connection();

    public static Connection getInstance() {
        return ourInstance;
    }

    private Connection() {
        startConnection();
    }
    /*
    public static DataInputStream getIn() {
        return in;
    }

    public static DataOutputStream getOut() {
        return out;
    }*/

    public void startConnection() {
        try {
            socket = new Socket(SERVER_ADDR, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            netState = "Соединение с сервером установлено";
            System.out.println("Соединение с сервером установлено");
        } catch (IOException e) {
            netState = "Сервер недоступен";
/*            e.printStackTrace();*/
        }
    }

    public String getNetState() {
        return netState;
    }

    /*    public void restartConnection() {
        Timer restartTimer = new Timer();
        TimerTask restartTask = new TimerTask() {
            @Override
            public void run() {
                System.out.println("Попытка переподключения к серверу");
                startConnection();
            }
        };
        restartTimer.schedule(restartTask, 0, 3000);
    }*/

    // clients authentication method
    public String auth(String login, String pass) {
        try {
            if (out !=null && in != null) {
                out.writeUTF("/check");
                in.readUTF();
            } else {
                startConnection();
                return "/reconnect";
            }
        } catch (SocketException e) {
            startConnection();
            return "/reconnect";
        } catch (IOException e) {
            startConnection();
            return "/reconnect";
        }
        sendMessage("/auth " + login + " " + pass);
        String response = receiveMessage();
        if (response.startsWith("/authok")) {
            String[] elements = response.split(" ");
            currentNick = elements[1];
            wasConnected = true;
            return "/success";
        }
        return response;
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String receiveMessage() {
        String msg = null;
        try {
            msg = in.readUTF();
        } catch (EOFException e) {
            //catch end of file condition
        } catch (IOException e) {e.printStackTrace();}
        return msg;
    }
}
