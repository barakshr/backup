package com.is.infra.http;

/**
 * Thrown when a polling condition is not met within the configured max wait time.
 * Carries the last observed value (POJO or {@link ApiResponse}) for debugging.
 */
public class PollingTimeoutException extends RuntimeException {

    private final Object lastValue;

    public PollingTimeoutException(String message, Object lastValue) {
        super(message);
        this.lastValue = lastValue;
    }

    public PollingTimeoutException(String message, Object lastValue, Throwable cause) {
        super(message, cause);
        this.lastValue = lastValue;
    }

    /** The last successfully obtained value before timeout, or {@code null} if no call succeeded. */
    public Object getLastValue() {
        return lastValue;
    }
}
