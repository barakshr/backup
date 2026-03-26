package com.is.deepfake.clients;

import static org.awaitility.Awaitility.await;

import java.time.Duration;
import java.util.Map;

import com.is.deepfake.config.DeepFakeConfig;
import com.is.deepfake.dto.DemoToolCallRequest;
import com.is.infra.config.ConfigManager;
import com.is.infra.http.ApiResponse;
import com.is.infra.http.BaseApiClient;
import com.is.infra.http.CookieAuthProvider;
import com.is.infra.http.HttpClientProperties;

/**
 * Client for the IronScales DemoTool API.
 * <p>
 * Auth: cookie-based (POST /auth/login → JWT in Set-Cookie → Cookie on all
 * requests).
 * Config: {@code demo.tool.*} keys via {@link ConfigManager}.
 * HTTP tuning: {@code http.*} keys via {@link HttpClientProperties}.
 * <p>
 * Endpoints:
 * GET /calls/status — current call status
 * GET /dashboard/calls — list all calls
 * POST /calls/join-multiple — join a Teams meeting as a deepfake participant
 * POST /calls/leave-call — leave a call
 * POST /calls/trigger-popup — trigger the deepfake popup
 */
public class DemoToolClient extends BaseApiClient {

    private static final String CALL_STATUS_PATH = "/calls/status";
    private static final String DASHBOARD_PATH = "/dashboard/calls";
    private static final String JOIN_PATH = "/calls/join-multiple";
    private static final String LEAVE_PATH = "/calls/leave-call";
    private static final String TRIGGER_POPUP_PATH = "/calls/trigger-popup";

    public DemoToolClient() {
        super(
                DeepFakeConfig.get().getDemoToolBaseUrl(),
                new CookieAuthProvider(
                        DeepFakeConfig.get().getBaseUrl(),
                        DeepFakeConfig.get().getDemoToolUsername(),
                        DeepFakeConfig.get().getDemoToolPassword()),
                new HttpClientProperties());
    }

    /**
     * Returns the status of all active calls.
     * The first call triggers lazy authentication via CookieAuthProvider.
     */
    public ApiResponse getCallStatus() {
        return get(CALL_STATUS_PATH);
    }

    /** Returns all calls visible in the DemoTool dashboard. */
    public ApiResponse getDashboardCalls() {
        return get(DASHBOARD_PATH);
    }

    /** Joins a Teams meeting as a deepfake participant. */
    public ApiResponse joinCall(DemoToolCallRequest request) {
        return post(JOIN_PATH, request);
    }

    /** Leaves an active call. */
    public ApiResponse leaveCall(String callId) {
        return post(LEAVE_PATH, Map.of("call_id", callId));
    }

    /** Triggers the deepfake popup for all participants in the meeting. */
    public ApiResponse triggerPopup(String callId) {
        return post(TRIGGER_POPUP_PATH, Map.of("call_id", callId));
    }

    public void getDashBoard( Object xx) {
       
  
        //  await().atLeast(null).pollInterval(Duration.ofSeconds(1)).until(() -> getDashboardCalls().getBody(). == 200);
    }
}
