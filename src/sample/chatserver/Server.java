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

    // checks if user with the same nick is already online
    public boolean isNickBusy(String nick) {
        for (ClientHandler c: clients) {
            if (c.getName().equals(nick)) return true;
        }
        return false;
    }

    // gathers nicknames of currently online users and returns them as sequence in String object
    // TODO remove "undefinied" entries
    public String makeOnlineList() {
        StringBuffer nicksOnline = new StringBuffer();
        nicksOnline.append("/currentonlinelist");
        for (ClientHandler c: clients) {
            nicksOnline.append(" " + c.getName());
        }
        System.out.println(nicksOnline.toString());
        return nicksOnline.toString();
    }

    // searches recipient among connected clients array using nickname from message's prefix, splits message and passes it to recipient's sendMessage
    // TODO implement substring method instead of append
    public void sendPrivateMessage(ClientHandler sender, String msg) {
        String[] elements = msg.split(" ");
        String nick = elements[1];
        StringBuffer message = new StringBuffer();
        for (int i = 2; i < elements.length; i++) {
            if (i == elements.length - 1) message.append(elements[i]);
            else message.append(elements[i] + " ");
        }
        ClientHandler client = null;
        for(ClientHandler c: clients) {
            if (c.getName().contains(nick)) client = c;
        }
        if (client != null) client.sendMessage("private message from " + sender.getName() + ": " + message.toString());
    }

    // removes client from array
    public void unSubscribeMe(ClientHandler c) {
        clients.remove(c);
    }
}

