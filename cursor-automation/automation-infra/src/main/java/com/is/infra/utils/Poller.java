package com.is.infra.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

/**
 * Polls a condition until it returns true or the timeout is exceeded.
 * Used for eventual consistency scenarios — e.g. waiting for a DB record to appear,
 * a notification to be sent, or a call status to change.
 *
 * Stub — basic implementation provided, to be enhanced with backoff strategies.
 *
 * Usage:
 *   Poller.waitUntil(() -> callServer.getCallStatus(callId).equals("COMPLETED"),
 *       Duration.ofSeconds(30), Duration.ofSeconds(2));
 */
public class Poller {

    private static final Logger log = LoggerFactory.getLogger(Poller.class);

    private Poller() {}

    /**
     * Polls until the supplier returns true, or until the timeout is exceeded.
     *
     * @param condition     a supplier that returns true when the condition is met
     * @param timeout       maximum time to wait
     * @param pollInterval  how long to wait between checks
     * @throws AssertionError if condition is not met within the timeout
     */
    public static void waitUntil(Supplier<Boolean> condition, Duration timeout, Duration pollInterval) {
        Instant deadline = Instant.now().plus(timeout);
        int attempt = 0;

        while (Instant.now().isBefore(deadline)) {
            attempt++;
            log.debug("Poller attempt #{}", attempt);

            try {
                if (Boolean.TRUE.equals(condition.get())) {
                    log.debug("Condition met after {} attempts", attempt);
                    return;
                }
            } catch (Exception e) {
                log.debug("Condition check threw exception on attempt {}: {}", attempt, e.getMessage());
            }

            try {
                Thread.sleep(pollInterval.toMillis());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new RuntimeException("Poller interrupted", e);
            }
        }

        throw new AssertionError(
                String.format("Condition not met within %s after %d attempts", timeout, attempt)
        );
    }

    /**
     * Polls with default interval of 2 seconds.
     */
    public static void waitUntil(Supplier<Boolean> condition, Duration timeout) {
        waitUntil(condition, timeout, Duration.ofSeconds(2));
    }
}
