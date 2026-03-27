package com.is.deepfake.clients;

import java.time.Duration;

import com.is.deepfake.dto.CallStatusResponse;
import com.is.infra.http.PollingCondition;

/**
 * Pre-built {@link PollingCondition} instances for the call-status endpoint.
 */
public final class CallStatusPoller {

    private CallStatusPoller() {}

    private static final Duration DEFAULT_MAX_WAIT = Duration.ofSeconds(60);
    private static final Duration DEFAULT_INTERVAL = Duration.ofSeconds(5);

    /** Poll until a password/token is present in the call status response. */
    public static final PollingCondition<CallStatusResponse> untilReady =
            PollingCondition.<CallStatusResponse>poll(DEFAULT_MAX_WAIT, DEFAULT_INTERVAL,
                    status -> status.getPassword() != null && !status.getPassword().isEmpty())
                    .ignoringExceptions();
}
