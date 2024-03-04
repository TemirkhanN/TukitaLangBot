package me.nasukhov.db;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            Class.forName("org.sqlite.JDBC");
            URL url = Thread.currentThread().getContextClassLoader().getResource("app.db");
            if (url == null) {
                throw new Exception("Database file not found in resources directory");
            }
            Path path = Paths.get(url.toURI());
            db = DriverManager.getConnection("jdbc:sqlite:" + path);
        } catch (Exception e) {
            e.printStackTrace();

            throw new RuntimeException("Error occurred while connecting to db");
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

    public boolean executeQuery(String query, Map<Integer, Object> params) {
        try {
            PreparedStatement stmt = db.prepareStatement(query);
            for (Map.Entry<Integer, Object> entry : params.entrySet()) {
                stmt.setObject(entry.getKey(), entry.getValue());
            }

            boolean result = stmt.execute();
            stmt.close();

            return result;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
