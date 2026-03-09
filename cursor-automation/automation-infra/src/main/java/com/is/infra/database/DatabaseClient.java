package com.is.infra.database;

/**
 * Base class for direct database access.
 * Stub — to be implemented when DB assertions are needed (stage 3).
 *
 * Intended usage:
 *   DatabaseClient db = new DatabaseClient(config);
 *   Map<String, Object> row = db.queryFirst("SELECT * FROM calls WHERE call_id = ?", callId);
 *   assertThat(row.get("status")).isEqualTo("COMPLETED");
 *
 * Will use JDBC with Testcontainers for isolated test DBs,
 * or connect directly to the test environment Postgres DB.
 */
public class DatabaseClient {

    public DatabaseClient() {
        throw new UnsupportedOperationException("DatabaseClient not yet implemented");
    }
}
