--
PRAGMA foreign_keys = off;
BEGIN TRANSACTION;

-- Таблица: users
DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    [key]    TEXT NOT NULL,
    login    TEXT UNIQUE
                  NOT NULL
        PRIMARY KEY,
    password TEXT NOT NULL,
    nickName TEXT UNIQUE
);

INSERT INTO users ([key],
                   login,
                   password,
                   nickName)
VALUES ('user1',
        'user1',
        'passwd1',
        '1_user');

INSERT INTO users ([key],
                   login,
                   password,
                   nickName)
VALUES ('user2',
        'user2',
        'passwd2',
        '2_user');

INSERT INTO users ([key],
                   login,
                   password,
                   nickName)
VALUES ('user3',
        'user3',
        'passwd3',
        '3_user');


-- Индекс: sqlite_autoindex_users_1
DROP INDEX IF EXISTS sqlite_autoindex_users_1;

CREATE INDEX sqlite_autoindex_users_1 ON users (
                                                login COLLATE BINARY
    );


-- Индекс: sqlite_autoindex_users_2
DROP INDEX IF EXISTS sqlite_autoindex_users_2;

CREATE INDEX sqlite_autoindex_users_2 ON users (
                                                nickName COLLATE BINARY
    );


COMMIT TRANSACTION;
PRAGMA foreign_keys = on;
