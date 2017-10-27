package sample.chatserver;

import sample.chatclient.Connection;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketTimeoutException;


public class ClientHandler {
    private Socket clientSocket;
    private Server server;
    private DataInputStream in;
    private DataOutputStream out;
    private String name;

    public ClientHandler(Socket clientSocket, Server server) {
        this.server = server;
        this.clientSocket = clientSocket;
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
            name = "undefined";
        } catch (IOException e) {
            e.printStackTrace();
        }
        new Thread( () -> {
            // TODO auth
            try {
                //Авторизация
                while (true) {
                    String msg = in.readUTF();
                    if (msg.startsWith("/auth")) {
                        String[] elements = msg.split(" ");
                        String nick = server.getAuthService().getNickLoginByPass(elements[1], elements[2]);
                        System.out.println(nick);
                        if (nick != null) { //если указаны верные учетные данные
                            if (!server.isNickBusy(nick)) {
                                sendMessage("/authok " + nick);
                                this.name = nick;
                                server.broadcast(this.name + " зашел в чат");
                                break;
                            } else sendMessage("Учетная запись уже используется");
                        } else sendMessage("Указаны неверные учетные данные");
                    } else sendMessage("Для начала нужно авторизоваться");
                } //конец цикла авторизации
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
            } catch (SocketTimeoutException e) {
                System.out.println(name + " отключен по таймауту");
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

    public void sendMessage(String msg) {
        try {
            System.out.println("sendMessage " + msg);
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
