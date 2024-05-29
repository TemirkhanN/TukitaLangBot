package me.nasukhov.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Collection implements AutoCloseable {
    private ResultSet rs;
    private Statement stmt;
    private boolean isFreed;

    public Collection(ResultSet rs, Statement stmt) {
        this.rs = rs;
        this.stmt = stmt;
        isFreed = false;
    }

    public boolean next() {
        if (isFreed) {
            return false;
        }

        try {
            if (rs.isClosed()) {
                return false;
            }
            return rs.next();
        } catch (SQLException e) {
            free();
            // TODO
            return false;
        }
    }

    public <T> T getCurrentEntryProp(String propName) {
        if (isFreed) {
            throw new RuntimeException("Resource is already freed");
        }

        try {
            return (T) rs.getObject(propName);
        } catch (SQLException e) {
            // TODO
            free();
            throw new RuntimeException();
        }
    }

    public void free() {
        if (isFreed) {
            return;
        }

        try {
            stmt.close();
            rs.close();
        } catch (SQLException ignored) {

        }
        stmt = null;
        rs = null;
        isFreed = true;
    }

    @Override
    public void close() throws Exception{
        free();
    }
}
