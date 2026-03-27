package com.is.deepfake.clients;

import java.util.Map;

import com.is.deepfake.config.DeepFakeConfig;
import com.is.deepfake.dto.CallStatusResponse;
import com.is.deepfake.dto.DemoToolCallRequest;
import com.is.deepfake.dto.DemoToolCallResponse;
import com.is.infra.http.ApiResponse;
import com.is.infra.http.BaseApiClient;
import com.is.infra.http.CookieAuthProvider;
import com.is.infra.http.HttpClientProperties;
import com.is.infra.http.PollingCondition;
import com.is.infra.http.TypedApiResponse;

/**
 * Client for the IronScales DemoTool API.
 * <p>
 * Auth: cookie-based (POST /auth/login → JWT in Set-Cookie → Cookie on all
 * requests).
 * Config: {@code demo.tool.*} keys via {@link com.is.infra.config.ConfigManager}.
 * HTTP tuning: {@code http.*} keys via {@link HttpClientProperties}.
 * <p>
 * Endpoints:
 * GET  /calls/status       — current call status
 * GET  /dashboard/calls    — list all calls
 * POST /calls/join-multiple — join a Teams meeting as a deepfake participant
 * POST /calls/leave-call   — leave a call
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

    // ─── call status ───

    public TypedApiResponse<CallStatusResponse> getCallStatus() {
        return new TypedApiResponse<>(get(CALL_STATUS_PATH), CallStatusResponse.class);
    }

    public TypedApiResponse<CallStatusResponse> getCallStatus(PollingCondition<CallStatusResponse> condition) {
        return getUntil(CALL_STATUS_PATH, CallStatusResponse.class, condition);
    }

    // ─── dashboard ───

    public ApiResponse getDashboardCalls() {
        return get(DASHBOARD_PATH);
    }

    public ApiResponse getDashboardCalls(PollingCondition<ApiResponse> condition) {
        return get("DASHBOARD_PATH", condition);
    }

    public TypedApiResponse<DemoToolCallResponse> getDashboardCallsTyped() {
        return new TypedApiResponse<>(get(DASHBOARD_PATH), DemoToolCallResponse.class);
    }

    public TypedApiResponse<DemoToolCallResponse> getDashboardCallsTyped(PollingCondition<DemoToolCallResponse> condition) {
        return getUntil(DASHBOARD_PATH, DemoToolCallResponse.class, condition);
    }

    // ─── actions ───

    public ApiResponse joinCall(DemoToolCallRequest request) {
        return post(JOIN_PATH, request);
    }

    public ApiResponse leaveCall(String callId) {
        return post(LEAVE_PATH, Map.of("call_id", callId));
    }

    public ApiResponse triggerPopup(String callId) {
        return post(TRIGGER_POPUP_PATH, Map.of("call_id", callId));
    }
}
