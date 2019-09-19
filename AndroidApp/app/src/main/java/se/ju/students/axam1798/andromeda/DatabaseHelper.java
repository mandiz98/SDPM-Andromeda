package se.ju.students.axam1798.andromeda;

import com.j256.ormlite.jdbc.JdbcConnectionSource;

import java.sql.SQLException;

public class DatabaseHelper {

    private static String URL = "jdbc:mysql://db4free.net/andromeda_db";
    private static String USERNAME = "andromeda";
    private static String PASSWORD = "123213213";

    public static JdbcConnectionSource getConnection() throws SQLException {
        return new JdbcConnectionSource(URL, USERNAME, PASSWORD);
    }

}