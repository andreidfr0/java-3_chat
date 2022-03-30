package com.geekbrains.server.authorization;

import java.sql.SQLException;

public interface AuthService {
    void start();

    String getNickNameByLoginAndPassword(String login, String password);

    void end();

    boolean getSqlField();

    boolean connect();

    void disconnect() throws SQLException;

    //    void findAll() throws SQLException;
    void updateNickName(String nick, String newNick);
}
