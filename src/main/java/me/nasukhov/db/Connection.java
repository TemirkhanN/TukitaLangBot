package me.nasukhov.db;

import java.sql.*;
import java.util.Map;

public class Connection {
    private final java.sql.Connection db;

    private static Connection instance;

    public static Connection getInstance() {
        if (Connection.instance == null) {
            Connection.instance = new Connection();
        }

        return Connection.instance;
    }

    private Connection() {
        try {
            String url = System.getenv("DATABASE_URL");
            String username = System.getenv("DATABASE_USERNAME");
            String password = System.getenv("DATABASE_PASSWORD");

            db = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException("Error occurred while connecting to db");
        }
    }

    public ResultSet fetchByQuery(String query) {
        try {
            Statement stmt = db.createStatement();

            return stmt.executeQuery(query);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public ResultSet fetchByQuery(String query, Map<Integer, Object> params) {
        try {
            PreparedStatement stmt = db.prepareStatement(query);
            for (Map.Entry<Integer, Object> entry : params.entrySet()) {
                stmt.setObject(entry.getKey(), entry.getValue());
            }

            return stmt.executeQuery();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void executeQuery(String query, Map<Integer, Object> params) {
        try {
            PreparedStatement stmt = db.prepareStatement(query);
            for (Map.Entry<Integer, Object> entry : params.entrySet()) {
                stmt.setObject(entry.getKey(), entry.getValue());
            }

            stmt.execute();
            stmt.close();

        } catch (SQLException e) {
            // TODO
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    public void executeQuery(String query) {
        try {
            Statement stmt = db.createStatement();

            boolean result = stmt.execute(query);
            stmt.close();
        } catch (SQLException e) {
            // TODO
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }
}
