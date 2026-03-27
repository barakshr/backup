package com.is.infra.http;

import java.time.Duration;
import java.util.function.Predicate;

/**
 * Configures polling behavior for API calls that need to wait
 * until a condition on the response is met.
 * <p>
 * Works with both raw {@link ApiResponse} and typed POJOs.
 * All polling parameters are caller-defined; the condition is always optional
 * at the client method level (overloaded methods).
 *
 * @param <T> the type the predicate operates on — either a POJO or {@link ApiResponse}
 */
public class PollingCondition<T> {

    private final Duration maxWait;
    private final Duration pollInterval;
    private final Predicate<T> until;
    private Predicate<T> failWhen;
    private boolean ignoreExceptions;

    private PollingCondition(Duration maxWait, Duration pollInterval, Predicate<T> until) {
        this.maxWait = maxWait;
        this.pollInterval = pollInterval;
        this.until = until;
    }

    /**
     * Creates a polling condition.
     *
     * @param maxWait      maximum time to poll before throwing {@link PollingTimeoutException}
     * @param pollInterval time between each poll attempt
     * @param until        predicate that must return {@code true} for polling to succeed
     */
    public static <T> PollingCondition<T> poll(Duration maxWait, Duration pollInterval, Predicate<T> until) {
        return new PollingCondition<>(maxWait, pollInterval, until);
    }

    /**
     * If the API call itself throws (connection refused, 500, deserialization error, etc.),
     * swallow the exception and keep polling instead of failing immediately.
     */
    public PollingCondition<T> ignoringExceptions() {
        this.ignoreExceptions = true;
        return this;
    }

    /**
     * Abort polling immediately if this condition is detected (unrecoverable state).
     * Throws {@link PollingTimeoutException} with a "fail-fast" message.
     */
    public PollingCondition<T> failWhen(Predicate<T> failCondition) {
        this.failWhen = failCondition;
        return this;
    }

    public Duration getMaxWait()        { return maxWait; }
    public Duration getPollInterval()   { return pollInterval; }
    public Predicate<T> getUntil()      { return until; }
    public Predicate<T> getFailWhen()   { return failWhen; }
    public boolean isIgnoreExceptions() { return ignoreExceptions; }
}
