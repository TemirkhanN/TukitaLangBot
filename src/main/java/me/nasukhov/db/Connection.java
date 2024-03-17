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
            db.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException("Error occurred while connecting to db");
        }
    }

    public Collection fetchByQuery(String query) {
        try {
            Statement stmt = db.createStatement();

            return new Collection(stmt.executeQuery(query), stmt);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Collection fetchByQuery(String query, Map<Integer, Object> params) {
        try {
            PreparedStatement stmt = db.prepareStatement(query);
            for (Map.Entry<Integer, Object> entry : params.entrySet()) {
                stmt.setObject(entry.getKey(), entry.getValue());
            }

            return new Collection(stmt.executeQuery(), stmt);
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

            stmt.execute(query);
            stmt.close();
        } catch (SQLException e) {
            // TODO
            e.printStackTrace();

            throw new RuntimeException(e);
        }
    }

    public boolean tableExists(String tableName) {
        try {
            Statement stmt = db.createStatement();
            ResultSet result = stmt.executeQuery(String.format("SELECT EXISTS (SELECT * FROM information_schema.tables WHERE table_name = '%s')", tableName));

            boolean exists = result.next() && result.getBoolean(1);

            result.close();
            stmt.close();

            return exists;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
