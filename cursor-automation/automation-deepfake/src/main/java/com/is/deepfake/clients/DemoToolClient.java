package com.is.deepfake.clients;

import com.is.deepfake.config.DemoToolProperties;
import com.is.deepfake.dto.DemoToolCallRequest;
import com.is.infra.http.ApiResponse;
import com.is.infra.http.BaseApiClient;
import com.is.infra.http.CookieAuthProvider;
import com.is.infra.http.HttpClientProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Client for the IronScales DemoTool API.
 *
 * Auth: cookie-based (POST /auth/login → JWT in Set-Cookie → Cookie on all requests).
 * Config: injected via DemoToolProperties (automation.demo-tool.*).
 * HTTP config: injected via HttpClientProperties (automation.http.*).
 *
 * Endpoints:
 *   GET  /calls/status        — current call status
 *   GET  /dashboard/calls     — list all calls
 *   POST /calls/join-multiple — join a Teams meeting as a deepfake participant
 *   POST /calls/leave-call    — leave a call
 *   POST /calls/trigger-popup — trigger the deepfake popup
 */
@Component
public class DemoToolClient extends BaseApiClient {

    private static final String LOGIN_PATH        = "/auth/login";
    private static final String CALL_STATUS_PATH  = "/calls/status";
    private static final String DASHBOARD_PATH    = "/dashboard/calls";
    private static final String JOIN_PATH         = "/calls/join-multiple";
    private static final String LEAVE_PATH        = "/calls/leave-call";
    private static final String TRIGGER_POPUP_PATH = "/calls/trigger-popup";

    public DemoToolClient(DemoToolProperties props, HttpClientProperties httpProps) {
        super(
            props.getBaseUrl(),
            new CookieAuthProvider(
                props.getBaseUrl() + LOGIN_PATH,
                props.getUsername(),
                props.getPassword()
            ),
            httpProps
        );
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
}
