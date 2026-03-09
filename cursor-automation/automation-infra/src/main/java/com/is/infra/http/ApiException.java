package com.is.infra.http;

/**
 * Thrown when an HTTP operation fails or a response does not meet expectations.
 * Carries rich context to simplify debugging: method, URL, status code, response body.
 */
public class ApiException extends RuntimeException {

    private final String method;
    private final String url;
    private final int statusCode;

    public ApiException(String method, String url, int statusCode, String message) {
        super(buildMessage(method, url, statusCode, message));
        this.method = method;
        this.url = url;
        this.statusCode = statusCode;
    }

    public ApiException(String method, String url, int statusCode, String message, Throwable cause) {
        super(buildMessage(method, url, statusCode, message), cause);
        this.method = method;
        this.url = url;
        this.statusCode = statusCode;
    }

    public String getMethod() { return method; }
    public String getUrl() { return url; }
    public int getStatusCode() { return statusCode; }

    private static String buildMessage(String method, String url, int statusCode, String message) {
        return String.format("[%s %s] HTTP %d — %s", method, url, statusCode, message);
    }
}
