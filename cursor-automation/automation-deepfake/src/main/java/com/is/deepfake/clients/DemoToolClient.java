package com.is.deepfake.clients;

import com.is.deepfake.config.DeepfakeConfig;
import com.is.deepfake.dto.DemoToolCallRequest;
import com.is.infra.http.ApiResponse;
import com.is.infra.http.BaseApiClient;
import com.is.infra.http.CookieAuthProvider;

import java.util.Map;

/**
 * Client for the IronScales DemoTool API.
 * Provides methods for login, call management, and deepfake simulation.
 *
 * Auth: cookie-based (POST /auth/login → JWT in Set-Cookie → Cookie header on all requests)
 *
 * Endpoints:
 *   GET  /calls/status        — get current call status
 *   GET  /dashboard/calls     — list all calls
 *   POST /calls/join-multiple — join a Teams meeting as a deepfake participant
 *   POST /calls/leave-call    — leave a call
 *   POST /calls/trigger-popup — trigger the deepfake popup in the meeting
 */
public class DemoToolClient extends BaseApiClient {

    private static final String LOGIN_PATH = "/auth/login";
    private static final String CALL_STATUS_PATH = "/calls/status";
    private static final String DASHBOARD_CALLS_PATH = "/dashboard/calls";
    private static final String JOIN_MULTIPLE_PATH = "/calls/join-multiple";
    private static final String LEAVE_CALL_PATH = "/calls/leave-call";
    private static final String TRIGGER_POPUP_PATH = "/calls/trigger-popup";

    public DemoToolClient(DeepfakeConfig config) {
        super(
            config.getDemoToolBaseUrl(),
            new CookieAuthProvider(
                config.getDemoToolBaseUrl() + LOGIN_PATH,
                config.getDemoToolUsername(),
                config.getDemoToolPassword()
            )
        );
    }

    public DemoToolClient(String baseUrl, String username, String password) {
        super(baseUrl, new CookieAuthProvider(baseUrl + LOGIN_PATH, username, password));
    }

    /**
     * Returns the status of all active calls.
     * Also used as the "get with token" endpoint in the login smoke test —
     * the first call triggers authentication automatically via CookieAuthProvider.
     *
     * @return ApiResponse with call status data
     */
    public ApiResponse getCallStatus() {
        return get(CALL_STATUS_PATH);
    }

    /**
     * Returns all calls visible in the DemoTool dashboard.
     *
     * @return ApiResponse with list of calls
     */
    public ApiResponse getDashboardCalls() {
        return get(DASHBOARD_CALLS_PATH);
    }

    /**
     * Joins a Teams meeting as a deepfake participant.
     *
     * @param request call parameters (video, meeting link, tenant, organizer)
     * @return ApiResponse with join result
     */
    public ApiResponse joinCall(DemoToolCallRequest request) {
        return post(JOIN_MULTIPLE_PATH, request);
    }

    /**
     * Leaves an active call.
     *
     * @param callId the call to leave
     * @return ApiResponse
     */
    public ApiResponse leaveCall(String callId) {
        return post(LEAVE_CALL_PATH, Map.of("call_id", callId));
    }

    /**
     * Triggers the deepfake popup for all participants in the meeting.
     *
     * @param callId the active call
     * @return ApiResponse
     */
    public ApiResponse triggerPopup(String callId) {
        return post(TRIGGER_POPUP_PATH, Map.of("call_id", callId));
    }
}
