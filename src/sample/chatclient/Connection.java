package sample.chatclient;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class Connection {
    private static final String SERVER_ADDR = "localhost";
    private static final int SERVER_PORT = 4000;
    private static DataInputStream in;
    private static DataOutputStream out;
    private String currentNick;

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

    public static DataInputStream getIn() {
        return in;
    }

    public static DataOutputStream getOut() {
        return out;
    }

    public void startConnection() {
        try {
            Socket socket = new Socket(SERVER_ADDR, SERVER_PORT);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
            System.out.println("Соединение с сервером установлено");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String auth(String login, String pass) {
        sendMessage("/auth " + login + " " + pass);
        String response = receiveMessage();
        if (response.startsWith("/authok")) {
            String[] elements = response.split(" ");
            currentNick = elements[1];
            return "success";
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
        } catch (IOException e) {e.printStackTrace();}
        return msg;
    }
}
