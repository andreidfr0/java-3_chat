package com.geekbrains.server.authorization;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class InMemoryAuthServiceImpl implements AuthService {
    // create a connection to the database
    private static Map<String, UserData> users;
    private Statement statement;
    private Connection conn = null;

    public InMemoryAuthServiceImpl() {
        if (!getSqlField()) { // загрузим данные из БД если она существует
            users = new HashMap<>();
            users.put("login1", new UserData("login1", "password1", "first_user"));
            users.put("login2", new UserData("login2", "password2", "second_user"));
            users.put("login3", new UserData("login3", "password3", "third_user"));
        }
    }

    @Override
    public void start() {
        System.out.println("Сервис аутентификации инициализирован");
    }

    @Override
    public synchronized String getNickNameByLoginAndPassword(String login, String password) {
        UserData user = users.get(login);
        // Ищем пользователя по логину и паролю, если нашли то возвращаем никнэйм
        if (user != null && user.getPassword().equals(password)) {
            return user.getNickName();
        }

        return null;
    }

    @Override
    public void end() {
        System.out.println("Сервис аутентификации отключен");
    }


    // in ?отдельный класс? :(
    @Override
    public boolean getSqlField() {
        return connect();
    }

    @Override
    public boolean connect() { // connect to DB
        try {
            disconnect();
            conn = DriverManager.getConnection(SqlData.URL);
            statement = conn.createStatement();
//            findAll();
            System.out.println("Connection to SQLite has been established.");
            if (users == null) {
                users = new HashMap<>();
            } else {
                users.clear();
            }
            try (ResultSet rs = statement.executeQuery("select * from users;")) {
                while (rs.next()) {
                    users.put(rs.getString(1), new UserData(rs.getString(2), rs.getString(3), rs.getString(4)));
                }
            }
            return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                disconnect();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
        return false;
    }

    @Override
    public void disconnect() throws SQLException {
        if (conn != null) {
            conn.close();
            System.out.println("Connection to SQLite closed.");
        }
    }

//    @Override
//    public void findAll() throws SQLException { // extract user-tables data to memory Map<String, UserData> users
//        users = new HashMap<>();
//        try (ResultSet rs = statement.executeQuery("select * from users;")) {
//            while (rs.next()) {
//                users.put(rs.getString(1), new UserData(rs.getString(2), rs.getString(3), rs.getString(4)));
//            }
//        }
//    }

    @Override
    public void updateNickName(String nick, String newNick) { // обновляем запись в БД nickName
        try {
            conn = DriverManager.getConnection(SqlData.URL);
            System.out.println("Connection to SQLite has been established.");
            String sql = "update users set nickName = ? where nickname = ?;";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, newNick);
            ps.setString(2, nick);
            int log = ps.executeUpdate();
            System.out.println("Updated record: " + log);
//            findAll();
            connect();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                disconnect();
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

}

