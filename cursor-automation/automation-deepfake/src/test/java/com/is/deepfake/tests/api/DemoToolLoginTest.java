package com.is.deepfake.tests.api;

import com.is.common.testng.annotation.CommonAnnotation;
import com.is.common.testng.context.CommonContextHolder;
import com.is.deepfake.clients.DemoToolClient;
import com.is.deepfake.testng.DeepfakeBaseTest;
import com.is.deepfake.testng.annotation.DeepfakeAnnotation;
import com.is.deepfake.testng.context.DeepfakeContextHolder;
import com.is.infra.http.ApiResponse;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

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

  

   // @InfraAnnotation(cleanDatabase = true)
    @CommonAnnotation(createCompany = true)
    @DeepfakeAnnotation(createDfsTenant = true, joinTeamsMeeting = true)
    @Test(description = "DemoTool: login and verify authenticated GET /calls/status returns 200")
    public void loginAndGetCallStatus() {
        DemoToolClient demoToolClient = new DemoToolClient();
        ApiResponse response = demoToolClient.getCallStatus();
        CommonContextHolder.get().getCompany();
        DeepfakeContextHolder.get().getDfsTenant();

        assertThat(response.getStatusCode())
                .as("GET /calls/status should return 200 after successful login")
                .isEqualTo(200);

        assertThat(response.getBody())
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
}
