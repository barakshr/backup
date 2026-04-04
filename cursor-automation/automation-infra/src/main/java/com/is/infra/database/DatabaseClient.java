package com.is.infra.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic JDBC wrapper for direct database assertions in service-level tests.
 * Zero product knowledge — any module can use this to query any Postgres DB.
 * <p>
 * Typical usage from a test:
 * <pre>
 *   DatabaseClient db = new DatabaseClient(host, port, dbName, user, password);
 *   Map&lt;String, Object&gt; row = db.queryFirst("SELECT * FROM calls WHERE call_id = ?", callId);
 *   assertThat(row.get("tenant_id")).isEqualTo(expectedTenantId);
 *   db.close();
 * </pre>
 */
public class DatabaseClient implements AutoCloseable {

    private static final Logger log = LoggerFactory.getLogger(DatabaseClient.class);

    private final Connection connection;

    public DatabaseClient(String host, int port, String dbName, String user, String password) {
        String url = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
        try {
            this.connection = DriverManager.getConnection(url, user, password);
            log.info("DB connection established: {}@{}:{}/{}", user, host, port, dbName);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to connect to database: " + url, e);
        }
    }

    /**
     * Executes a query and returns the first row as a column-name → value map.
     *
     * @return the first row, or null if the result set is empty
     */
    public Map<String, Object> queryFirst(String sql, Object... params) {
        List<Map<String, Object>> rows = queryAll(sql, params);
        return rows.isEmpty() ? null : rows.get(0);
    }

    /**
     * Executes a query and returns all rows.
     */
    public List<Map<String, Object>> queryAll(String sql, Object... params) {
        log.debug("Executing SQL: {} | params: {}", sql, params);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                return toMapList(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Query failed: " + sql, e);
        }
    }

    /**
     * Returns true if at least one row matches.
     */
    public boolean rowExists(String sql, Object... params) {
        return queryFirst(sql, params) != null;
    }

    /**
     * Executes an INSERT/UPDATE/DELETE and returns the number of affected rows.
     */
    public int execute(String sql, Object... params) {
        log.debug("Executing update: {} | params: {}", sql, params);
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                stmt.setObject(i + 1, params[i]);
            }
            return stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Update failed: " + sql, e);
        }
    }

    @Override
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                log.info("DB connection closed");
            }
        } catch (SQLException e) {
            log.warn("Error closing DB connection: {}", e.getMessage());
        }
    }

    private List<Map<String, Object>> toMapList(ResultSet rs) throws SQLException {
        ResultSetMetaData meta = rs.getMetaData();
        int columnCount = meta.getColumnCount();
        List<Map<String, Object>> rows = new ArrayList<>();

        while (rs.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(meta.getColumnLabel(i), rs.getObject(i));
            }
            rows.add(row);
        }
        return rows;
    }
}
