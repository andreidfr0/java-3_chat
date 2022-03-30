package com.geekbrains.server;

import com.geekbrains.CommonConstants;
import com.geekbrains.server.authorization.AuthService;
import com.geekbrains.server.authorization.InMemoryAuthServiceImpl;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {
    private final AuthService authService;

    private List<ClientHandler> connectedUsers;

    public Server() {
        authService = new InMemoryAuthServiceImpl();
        try (ServerSocket server = new ServerSocket(CommonConstants.SERVER_PORT)) {
            authService.start();
            connectedUsers = new ArrayList<>();
            while (true) {
                System.out.println("Сервер ожидает подключения");
                Socket socket = server.accept();
                System.out.println("Клиент подключился");
                new ClientHandler(this, socket);
            }
        } catch (IOException exception) {
            System.out.println("Ошибка в работе сервера");
            exception.printStackTrace();
        } finally {
            if (authService != null) {
                authService.end();
            }
        }
    }

    public AuthService getAuthService() {
        return authService;
    }

    public boolean isNickNameBusy(String nickName) {
        for (ClientHandler handler : connectedUsers) {
            if (handler.getNickName().equals(nickName)) {
                return true;
            }
        }

        return false;
    }

    public synchronized void broadcastMessage(String message) {
        for (ClientHandler handler : connectedUsers) {
            handler.sendMessage(message);
        }
    }

    public synchronized void toPrivMsgClient(ClientHandler from, String nickTo, String msg) {
        // private message send
        for (ClientHandler o : connectedUsers) {
            if (o.getNickName().equals(nickTo)) {
                o.sendMessage("from: " + from.getNickName() + ": " + msg);
                from.sendMessage("to: " + nickTo + ": " + msg);
                return;
            }
        }
        from.sendMessage("user " + nickTo + " is not connected!");
    }


    //-----------------change nickName old
//    public synchronized void changeNickClient(ClientHandler from, String nick, String nickTo) {
//        for (ClientHandler o : connectedUsers) {
//            if (o.getNickName().equals(nick)) {
//                o.sendMessage("user " + from + " change nick to:" + nickTo);
//                from.setNickName(nickTo);
//                return;
//            }
//        }
//        from.sendMessage("user " + nickTo + " is not connected!");
//    }

    //-----------------change nickName
    public synchronized void changeNickClient(ClientHandler from, String nick, String nickTo) {
        for (ClientHandler o : connectedUsers) {
            if (o.getNickName().equals(nick)) {
                o.setNickName(nickTo);
                authService.updateNickName(nick, nickTo);
                broadcastMessage("user " + nick + " change nick : " + nickTo);
                // предложено
                broadcastMessage(nickTo + " " + ServerCommandConstants.LOGIN);
                // предложено
                return;
            }
        }
        from.sendMessage("user " + nickTo + " is not connected!");
    }
//-----------------change nickName


    public synchronized void addConnectedUser(ClientHandler handler) {
        connectedUsers.add(handler);
    }

    public synchronized void disconnectUser(ClientHandler handler) {
        connectedUsers.remove(handler);
    }

    public String getClients() {
        StringBuilder builder = new StringBuilder("/clients ");
        for (ClientHandler user : connectedUsers) {
            builder.append(user.getNickName()).append("\n");
        }

        return builder.toString();
    }
}
