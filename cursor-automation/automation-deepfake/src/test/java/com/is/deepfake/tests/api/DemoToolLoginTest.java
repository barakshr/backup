package com.is.deepfake.tests.api;

import com.is.common.testng.annotation.CommonAnnotation;
import com.is.common.testng.context.CommonContextHolder;
import com.is.deepfake.clients.DemoToolClient;
import com.is.deepfake.testng.DeepfakeBaseTest;
import com.is.deepfake.testng.annotation.DeepfakeAnnotation;
import com.is.deepfake.testng.context.DeepfakeContextHolder;
import com.is.infra.http.ApiResponse;
import com.is.infra.testng.annotation.InfraAnnotation;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Smoke tests for the DemoTool API.
 *
 * Auth flow (handled automatically by CookieAuthProvider):
 *   1. POST /auth/login with form credentials
 *   2. Capture JWT from Set-Cookie on the 303 redirect
 *   3. Attach cookie to all subsequent requests
 *
 * Credentials are loaded from application.yml or environment variables:
 *   DEMO_TOOL_BASE_URL, DEMO_TOOL_USERNAME, DEMO_TOOL_PASSWORD
 */
public class DemoToolLoginTest extends DeepfakeBaseTest {

    @Autowired
    private DemoToolClient demoToolClient;

    @InfraAnnotation(cleanDatabase   = true)
    @CommonAnnotation(createCompany = true )
    @DeepfakeAnnotation(createDfsTenant = true ,joinTeamsMeeting = true)
  
   
    @Test(description = "DemoTool: login and verify authenticated GET /calls/status returns 200")
    public void loginAndGetCallStatus() {
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
        ApiResponse response = demoToolClient.getDashboardCalls();

        assertThat(response.getStatusCode())
                .as("GET /dashboard/calls should return 200 after successful login")
                .isEqualTo(200);

        assertThat(response.getBody())
                .as("Response body should not be empty")
                .isNotBlank();
    }
}
