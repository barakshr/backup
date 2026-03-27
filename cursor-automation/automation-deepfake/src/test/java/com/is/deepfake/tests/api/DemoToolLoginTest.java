package com.is.deepfake.tests.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Duration;

import org.testng.annotations.Test;

import com.is.common.testng.annotation.CommonAnnotation;
import com.is.common.testng.context.CommonContextHolder;
import com.is.deepfake.clients.CallStatusPoller;
import com.is.deepfake.clients.DashboardPoller;
import com.is.deepfake.clients.DemoToolClient;
import com.is.deepfake.dto.CallStatusResponse;
import com.is.deepfake.dto.DemoToolCallResponse;
import com.is.deepfake.testng.DeepfakeBaseTest;
import com.is.deepfake.testng.annotation.DeepfakeAnnotation;
import com.is.deepfake.testng.context.DeepfakeContextHolder;
import com.is.infra.http.ApiResponse;
import com.is.infra.http.PollingCondition;

/**
 * Smoke tests for the DemoTool API.
 * <p>
 * Auth flow (handled automatically by CookieAuthProvider):
 *   1. POST /auth/login with form credentials
 *   2. Capture JWT from Set-Cookie on the 303 redirect
 *   3. Attach cookie to all subsequent requests
 * <p>
 * Credentials and URLs: {@code config.properties} or env vars (e.g. {@code DEMO_TOOL_BASE_URL}).
 */
public class DemoToolLoginTest extends DeepfakeBaseTest {

    @CommonAnnotation(createCompany = true)
    @DeepfakeAnnotation(createDfsTenant = true, joinTeamsMeeting = true)
    @Test(description = "DemoTool: login and verify authenticated GET /calls/status returns 200")
    public void loginAndGetCallStatus() {
        DemoToolClient demoToolClient = new DemoToolClient();

        demoToolClient.getCallStatus().getBodyAsObject().getPassword();

        CommonContextHolder.get().getCompany();
        DeepfakeContextHolder.get().getDfsTenant();

        assertThat(demoToolClient.getCallStatus().getStatusCode())
                .as("GET /calls/status should return 200 after successful login")
                .isEqualTo(200);

        assertThat(demoToolClient.getCallStatus().getBody())
                .as("Response body should not be empty")
                .isNotBlank();
    }

    @Test(description = "DemoTool: login and verify authenticated GET /dashboard/calls returns 200")
    public void loginAndGetDashboardCalls() {
        DemoToolClient demoToolClient = new DemoToolClient();
        ApiResponse response = demoToolClient.getDashboardCalls();

        assertThat(response.getStatusCode())
                .as("GET /dashboard/calls should return 200 after successful login")
                .isEqualTo(200);

        assertThat(response.getBody())
                .as("Response body should not be empty")
                .isNotBlank();
    }

    // ─── Polling examples ───

    @Test(description = "Poll raw ApiResponse with pre-built poller")
    public void pollDashboardUntilOk() {
        DemoToolClient client = new DemoToolClient();

        ApiResponse resp = client.getDashboardCalls(DashboardPoller.untilOk);
        assertThat(resp.getStatusCode()).isEqualTo(200);
    }

    @Test(description = "Poll typed POJO with pre-built poller")
    public void pollDashboardUntilActive() {
        DemoToolClient client = new DemoToolClient();

        DemoToolCallResponse data = client.getDashboardCallsTyped(DashboardPoller.untilActive)
                .getBodyAsObject();
        assertThat(data.getStatus()).isEqualTo("active");
    }

    @Test(description = "Poll typed POJO with custom status")
    public void pollDashboardUntilCustomStatus() {
        DemoToolClient client = new DemoToolClient();
 
        DemoToolCallResponse data = client.getDashboardCallsTyped(DashboardPoller.untilStatus("completed"))
                .getBodyAsObject();
        assertThat(data.getStatus()).isEqualTo("completed");
    }

    @Test(description = "Poll call status with pre-built poller")
    public void pollCallStatusUntilReady() {
        DemoToolClient client = new DemoToolClient();

        CallStatusResponse status = client.getCallStatus(CallStatusPoller.untilReady)
                .getBodyAsObject();
        assertThat(status.getPassword()).isNotBlank();
    }

    @Test(description = "Inline condition for one-off cases")
    public void pollWithInlineCondition() {
        DemoToolClient client = new DemoToolClient();

        DemoToolCallResponse data = client.getDashboardCallsTyped(
                PollingCondition.poll(
                        Duration.ofSeconds(45),
                        Duration.ofSeconds(3),
                        (DemoToolCallResponse d) -> "special-state".equals(d.getStatus())
                ).ignoringExceptions()
        ).getBodyAsObject();

        assertThat(data.getStatus()).isEqualTo("special-state");
    }
}
