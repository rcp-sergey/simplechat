package sample.chatserver;

import sample.chatclient.Connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.Timer;
import java.util.TimerTask;


public class ClientHandler {
    private final static int TIME_OUT = 12000;
    private Socket clientSocket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;
    private Timer disconnectTimer;

    public ClientHandler(Socket clientSocket, Server server) {
        this.server = server;
        this.clientSocket = clientSocket;
        setNotLoggedInDisconnection(); // enable disconnection by timeout until client is authorized
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            name = "undefined";
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread( () -> {
            try {
                //Авторизация
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/auth")) {
                        String[] elements = msg.split(" ");
                        String nick = server.getAuthService().getNickLoginByPass(elements[1], elements[2]);
                        System.out.println(nick);
                        if (nick != null) { // если указаны верные учетные данные
                            if (!server.isNickBusy(nick)) {
                                sendMessage("/authok " + nick);
                                this.name = nick;
                                server.broadcast(this.name + " зашел в чат");
                                break;
                            } else sendMessage("Учетная запись уже используется");
                        } else sendMessage("Указаны неверные учетные данные");
                    } else sendMessage("Для начала нужно авторизоваться");
                } //конец цикла авторизации
                disconnectTimer.cancel(); // disable disconnection by time out task
                System.out.println("Авторизация завершена");
                server.sendOnlineList();
                while (true) {
                    String msg = in.readUTF();
                    System.out.println(msg);
                    if (msg.equalsIgnoreCase("/end")) break;
                    if (msg.startsWith("/w")) {
                        server.sendPrivateMessage(this, msg);
                    } else server.broadcast(this.name + " " + msg);
                }
            } catch (SocketException e) {
                System.out.println("Сокет клиента " + name + " отключен");
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                server.unSubscribeMe(this);
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setNotLoggedInDisconnection() {
        ClientHandler clientHandler = this;
        TimerTask disconnectTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    clientSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
/*                server.unSubscribeMe(clientHandler);*/
                System.out.println("Клиент " + name + " отключен по таймауту");
            }
        };
        disconnectTimer = new Timer();
        disconnectTimer.schedule(disconnectTask, TIME_OUT);
    }

    public void sendMessage(String msg) {
        try {
            out.writeUTF(msg);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getName() {
        return this.name;
    }
}
