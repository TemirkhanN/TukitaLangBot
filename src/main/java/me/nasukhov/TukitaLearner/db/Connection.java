package me.nasukhov.TukitaLearner.db;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.Map;

@Component
public class Connection {
    private final java.sql.Connection db;

    public Connection(
            @Value("${DATABASE_URL}") String url,
            @Value("${DATABASE_USERNAME}") String username,
            @Value("${DATABASE_PASSWORD}") String password
    ) {
        try {
            db = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
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
