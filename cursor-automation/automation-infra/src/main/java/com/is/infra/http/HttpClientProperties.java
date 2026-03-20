package com.is.infra.http;

import com.is.infra.config.ConfigManager;

/**
 * HTTP client tuning for RestAssured-backed API clients.
 * Values are read from {@link ConfigManager} keys (with defaults):
 * <ul>
 *   <li>{@code http.read.timeout} — seconds (default 60)</li>
 *   <li>{@code http.connection.timeout} — seconds (default 30)</li>
 *   <li>{@code http.log.requests} — boolean (default true)</li>
 * </ul>
 */
public class HttpClientProperties {

    private final int readTimeout;
    private final int connectionTimeout;
    private final boolean logRequests;

    public HttpClientProperties() {
        ConfigManager cfg = ConfigManager.get();
        this.readTimeout = cfg.getInt("http.read.timeout", 60);
        this.connectionTimeout = cfg.getInt("http.connection.timeout", 30);
        this.logRequests = cfg.getBoolean("http.log.requests", true);
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    public boolean isLogRequests() {
        return logRequests;
    }
}
