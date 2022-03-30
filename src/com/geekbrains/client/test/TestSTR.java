package com.geekbrains.client.test;

import com.geekbrains.client.ClientConstants;

import java.io.*;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class TestSTR {
    static String historyFile;
    static File file;
    static File oldfile;
    static String nickUser;
    static boolean rot;
    static int stst;
    static String userName;
    public static Random rand = new Random();

    public static void main(String[] args) throws IOException {
        if (args.equals("")) {
            userName = "user1";
            setNameHistory(userName);
            write2(120);
        }

        for (int i = 0; i < args.length; i++) {
            setNameHistory(args[i]);
            write2(111);
        }
//        for (int i = 0; i < args.length; i++) {
//            setNameHistory(args[i]);
//            return100history();
//        }
    }

    public static void write2(int size) {
        stst = rand.nextInt(30) + 60;
        StringBuilder a = new StringBuilder();
        for (int i = 1; i < size; i++) {
            for (int u = 1; u < 25; u++) a.append((char) (rand.nextInt(30) + 60));
            a.append("\n" + i + "\t");
        }
        try {
            FileWriter fw = new FileWriter(file, true);
            BufferedWriter bw = new BufferedWriter(fw);
            bw.append(a.append(stst));
            bw.append("\n");
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected static void setNameHistory(String userName) throws IOException {
        nickUser = userName;
        historyFile = nickUser + ClientConstants.BLANCFILE;
        file = new File(historyFile);
        if (!file.exists()) {
            file.createNewFile();
        }
        oldfile = new File(nickUser + ClientConstants.BLANCOLDFILE);
    }

    public boolean rotateLog() {
        oldfile.delete();
        return file.renameTo(oldfile);
    }

    public static void return100history() {
        List list = new LinkedList<String>();
        if (file.exists()) {
            int valueStr = 0, num = 0, lastDg = 0;
            try (BufferedReader br = new BufferedReader(new FileReader(file), 1000)) {
                String c;
                while ((c = br.readLine()) != null) { //сейчас читает с головы - надо с хвоста
                    filter();
                    list.add(c);
                    valueStr = list.size();
//                    valueStr++;
                }
//                System.out.print("\nстрок считано" + valueStr + "\n");
                if (valueStr == 100) {
                    for (int i = 0; i < valueStr; i++) {
                        System.out.println(list.get(i));
                    }
                    System.out.println("=========================================================================================");

                } else if (valueStr < 100) {
                    int cc = valueStr - 100;
                    if (cc < 0) {
                        for (int i = 0; i < valueStr; i++) {
                            System.out.println(list.get(i));
                        }
                        System.out.println("=========================================================================================");
                    } else if (cc > 0) {
                        if (cc < 100) {
                            cc += 100 - cc;
                        }
                        for (int i = cc; i < valueStr; i++) {
                            System.out.println(list.get(i));
                        }
                        System.out.println("=========================================================================================");
                    }
                } else if (valueStr > 100) {
                    int cc = valueStr - 100;
                    for (int i = cc; i < valueStr; i++) {
                        System.out.println(list.get(i));
                    }
                    System.out.println("=========================================================================================");
                }

            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    private static void read(int cc) {


    }

    private static void filter() {
    }

    private static void filter(String msgText) {
        //       !!  убрать служебные слова !!
    }

}
