package com.geekbrains.client;

import com.geekbrains.CommonConstants;
import com.geekbrains.server.ServerCommandConstants;

import java.io.*;
import java.net.Socket;
import java.text.DateFormat;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Network {
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private final ChatController controller;
    protected String historyFile, oldfileName;
    protected File file;
    protected File oldfile;
    String nickUser;
    boolean stst, rot;

    public Network(ChatController chatController) {
        this.controller = chatController;
    }

    private void startReadServerMessages() throws IOException {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (true) {
                        //hw8 читаем из сокета что посылает server
                        String messageFromServer = inputStream.readUTF();
                        write2(messageFromServer);
                        System.out.println(messageFromServer);
                        if (messageFromServer.contains(ServerCommandConstants.LOGIN)) {
                            String[] client = messageFromServer.split(" ");
                            controller.displayClient(client[0]);
                            controller.displayMessage("Пользователь " + client[0] + " зашёл в чат");
                        } else if (messageFromServer.startsWith(ServerCommandConstants.EXIT)) {
                            String[] client = messageFromServer.split(" ");
                            controller.removeClient(client[1]);
                            controller.displayMessage(client[1] + " покинул чат");
                        } else if (messageFromServer.startsWith(ServerCommandConstants.CLIENTS)) {
                            String[] client = messageFromServer.split(" ");
                            for (int i = 1; i < client.length; i++) {
                                controller.displayClient(client[i]);
                            }
                        } else {
                            controller.displayMessage(messageFromServer);
                        }
                    }
                } catch (IOException exception) {
                    exception.printStackTrace();
                }
            }
        }).start();
    }

    private void initializeNetwork() throws IOException {
        socket = new Socket(CommonConstants.SERVER_ADDRESS, CommonConstants.SERVER_PORT);
        inputStream = new DataInputStream(socket.getInputStream());
        outputStream = new DataOutputStream(socket.getOutputStream());
    }

    public void sendMessage(String message) {
        try {
            outputStream.writeUTF(message);
        } catch (IOException exception) {
            exception.printStackTrace();
        }
    }

    public boolean sendAuth(String login, String password) {
        try {
            if (socket == null || socket.isClosed()) {
                initializeNetwork();
            }
            setNameHistory(login);
            outputStream.writeUTF(ServerCommandConstants.AUTHENTICATION + " " + login + " " + password);
            boolean authenticated = inputStream.readBoolean();
            if (authenticated) {
                startReadServerMessages();
            }
            return authenticated;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return false;
    }

    public void closeConnection() {
        try {
            outputStream.writeUTF(ServerCommandConstants.EXIT);
            outputStream.close();
            inputStream.close();
            socket.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }

        System.exit(1);
    }

    protected void setNameHistory(String userName) throws IOException {
        this.nickUser = userName;
        this.historyFile = userName + ClientConstants.BLANCFILE;
        this.file = new File(historyFile);
        if (!file.exists()) {
            boolean status = file.createNewFile();
            if (!status) throw new IOException();
        }
        oldfileName = nickUser + ClientConstants.BLANCOLDFILE;
        this.oldfile = new File(oldfileName);
        return100history();
        stst = rotateLog();
    }

    public boolean rotateLog() throws IOException {
        Date currentDate = new Date();
        Locale local = new Locale("ru", "RU");
        DateFormat df = DateFormat.getDateInstance(DateFormat.DEFAULT, local);
//        System.out.println("currentDate = " + df.format(currentDate));
        String zipFile = oldfileName + df.format(currentDate) + ".zip";
        ZipOutputStream zipOutputStream = new ZipOutputStream(new FileOutputStream(zipFile));
        try {
            File fileToZip = new File(oldfileName);
            FileInputStream fileInputStream = new FileInputStream(fileToZip);
            ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
            zipOutputStream.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fileInputStream.read(bytes)) >= 0) {
                zipOutputStream.write(bytes, 0, length);
            }

            zipOutputStream.close();
            fileInputStream.close();

            if (!fileToZip.delete()) throw new IOException();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.renameTo(oldfile);
    }

    private void filter(String msgText) {
        //       !!  убрать служебные слова !!
    }

    public void return100history() {
        List list = new LinkedList<String>();
        if (file.exists()) {
            int valueStr = 0, num = 0, lastDg = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(file), 1000)) {
                String c;
                while ((c = br.readLine()) != null) { //читает с головы
                    list.add(c);
                    valueStr = list.size();
                }
                if (valueStr == 100) {
                    for (int i = 0; i < valueStr; i++) {
                        System.out.println(list.get(i));
                        controller.displayMessage(list.get(i).toString());
                    }
                    controller.displayMessage("=========================================================================================");

                } else if (valueStr < 100) {
                    int cc = valueStr - 100;
                    if (cc < 0) {
                        for (int i = 0; i < valueStr; i++) {
                            System.out.println(list.get(i));
                            controller.displayMessage(list.get(i).toString());
                        }
                        controller.displayMessage("=========================================================================================");
                    } else if (cc > 0) {
                        if (cc < 100) {
                            cc += 100 - cc;
                        }
                        for (int i = cc; i < valueStr; i++) {
                            System.out.println(list.get(i));
                            controller.displayMessage(list.get(i).toString());
                        }
                        controller.displayMessage("=========================================================================================");
                    }
                } else if (valueStr > 100) {
                    int cc = valueStr - 100;
                    for (int i = cc; i < valueStr; i++) {
                        System.out.println(list.get(i));
                        controller.displayMessage(list.get(i).toString());
                    }
                    controller.displayMessage("=========================================================================================");
                }
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    public void write2(String isMsgText) {
        try {
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(isMsgText + "\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
