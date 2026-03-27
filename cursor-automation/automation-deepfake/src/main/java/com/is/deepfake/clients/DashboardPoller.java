package com.is.deepfake.clients;

import java.time.Duration;

import com.is.deepfake.dto.DemoToolCallResponse;
import com.is.infra.http.ApiResponse;
import com.is.infra.http.PollingCondition;

/**
 * Pre-built {@link PollingCondition} instances for the dashboard endpoint.
 * Keeps test code to a one-liner — no need to repeat timing or predicates.
 */
public final class DashboardPoller {

    private DashboardPoller() {}

    private static final Duration DEFAULT_MAX_WAIT = Duration.ofSeconds(30);
    private static final Duration DEFAULT_INTERVAL = Duration.ofSeconds(2);

    /** Poll until dashboard returns HTTP 200. */
    public static final PollingCondition<ApiResponse> untilOk =
            PollingCondition.<ApiResponse>poll(DEFAULT_MAX_WAIT, DEFAULT_INTERVAL,
                    r -> r.getStatusCode() == 200)
                    .ignoringExceptions();

    /** Poll until dashboard call status is "active". Fail-fast on "FAILED". */
    public static final PollingCondition<DemoToolCallResponse> untilActive =
            PollingCondition.<DemoToolCallResponse>poll(DEFAULT_MAX_WAIT, DEFAULT_INTERVAL,
                    dashboard -> "active".equals(dashboard.getStatus()))
                    .ignoringExceptions()
                    .failWhen(dashboard -> "FAILED".equals(dashboard.getStatus()));

    /** Poll until dashboard call reaches a specific status. */
    public static PollingCondition<DemoToolCallResponse> untilStatus(String status) {
        return PollingCondition.<DemoToolCallResponse>poll(DEFAULT_MAX_WAIT, DEFAULT_INTERVAL,
                dashboard -> status.equals(dashboard.getStatus()))
                .ignoringExceptions();
    }

    /** Poll with custom timing until dashboard call is "active". */
    public static PollingCondition<DemoToolCallResponse> untilActive(Duration maxWait, Duration interval) {
        return PollingCondition.<DemoToolCallResponse>poll(maxWait, interval,
                dashboard -> "active".equals(dashboard.getStatus()))
                .ignoringExceptions()
                .failWhen(dashboard -> "FAILED".equals(dashboard.getStatus()));
    }
}
