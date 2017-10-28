package sample.chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

public class Server {
    private static final int PORT = 4000;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private AuthService authService;
    private Vector<ClientHandler> clients;

    public AuthService getAuthService() {
        return authService;
    }

    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        serverSocket = null;
        clientSocket = null;
        clients = new Vector<>();
        try {
            serverSocket = new ServerSocket(PORT);
            authService = new BaseAuthService();
            authService.start();//заглушка
            System.out.println("Сервер онлайн, ждет подключений");
            while (true) {
                clientSocket = serverSocket.accept();
                clients.add(new ClientHandler(clientSocket, this));
                System.out.println("Клиент подключился");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    // sends message to all clients in array
    public void broadcast(String msg) {
        for (ClientHandler c : clients) {
            c.sendMessage(msg);
        }
    }

    // checks whether user with the same nick is already online
    public boolean isNickBusy(String nick) {
        for (ClientHandler c: clients) {
            if (c.getName().equals(nick)) return true;
        }
        return false;
    }

    // gathers nicknames of currently online users and returns them as sequence in String object
    public void sendOnlineList() {
        StringBuffer nicksOnline = new StringBuffer();
        nicksOnline.append("/currentonlinelist");
        for (ClientHandler c: clients) {
            if (!c.getName().equals("undefined"))
            nicksOnline.append(" " + c.getName());
        }
        System.out.println(nicksOnline.toString());
        broadcast(nicksOnline.toString());
    }

    // searches recipient among connected clients array using nickname from message's prefix, splits message and passes it to recipient's sendMessage
    public void sendPrivateMessage(ClientHandler sender, String msg) {
        // "msg" argument example: /w nick1 message text
        String nick = msg.split(" ")[1];
        ClientHandler recipient = null;
        for(ClientHandler c: clients) {
            if (c.getName().contains(nick)) recipient = c;
        }
        if (recipient == null) return;
        String privateMessage = msg.substring(msg.indexOf(" ") + nick.length(), msg.length());
        recipient.sendMessage("private from " + sender.getName() + ": " + privateMessage);
    }

    // removes client from array
    public void unSubscribeMe(ClientHandler c) {
        clients.remove(c);
        System.out.println("Подключенных клиентов: " + clients.size());
        if (clients.size() != 0) sendOnlineList();
    }
}

