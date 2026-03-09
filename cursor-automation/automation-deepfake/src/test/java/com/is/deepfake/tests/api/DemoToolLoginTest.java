package com.is.deepfake.tests.api;

import com.is.deepfake.tests.BaseDeepfakeApiTest;
import com.is.infra.http.ApiResponse;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke test for the DemoTool API.
 *
 * Test flow:
 *   1. POST /auth/login — authenticate with username + password (form-urlencoded)
 *   2. Capture JWT from Set-Cookie header on the 303 redirect response
 *   3. GET /calls/status — send request with the captured cookie
 *   4. Assert HTTP 200 and a valid response structure
 *
 * Prerequisites (set as env vars or in config.properties):
 *   demo.tool.base.url  → DEMO_TOOL_BASE_URL
 *   demo.tool.username  → DEMO_TOOL_USERNAME
 *   demo.tool.password  → DEMO_TOOL_PASSWORD
 */
public class DemoToolLoginTest extends BaseDeepfakeApiTest {

    @Test(description = "DemoTool: login and verify authenticated GET /calls/status returns 200")
    public void loginAndGetCallStatus() {
        // Authentication is handled automatically by CookieAuthProvider
        // on the first request — no explicit login call needed.
        ApiResponse response = demoToolClient.getCallStatus();

        assertThat(response.getStatusCode())
                .as("GET /calls/status should return 200 after successful login")
                .isEqualTo(200);

        assertThat(response.getBody())
                .as("Response body should not be empty")
                .isNotBlank();
    }

    @Test(description = "DemoTool: login and verify authenticated GET /dashboard/calls returns 200")
    public void loginAndGetDashboardCalls() {
        ApiResponse response = demoToolClient.getDashboardCalls();

        assertThat(response.getStatusCode())
                .as("GET /dashboard/calls should return 200 after successful login")
                .isEqualTo(200);

        assertThat(response.getBody())
                .as("Response body should not be empty")
                .isNotBlank();
    }
}
