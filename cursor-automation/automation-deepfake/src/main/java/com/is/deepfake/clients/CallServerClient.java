package com.is.deepfake.clients;

import com.is.deepfake.dto.CallTrackRequest;
import com.is.infra.http.ApiResponse;
import com.is.infra.http.BaseApiClient;
import com.is.infra.http.HttpClientProperties;

/**
 * Client for the Call Server API.
 * <p>
 * The Call Server is the brain of the DFS system. It receives call metadata
 * and participant screenshots from the Recording Bot, runs deepfake detection
 * via AI models, writes results to the DB, and triggers the Notification Bot.
 * <p>
 * In tests, this client simulates the Recording Bot's outgoing calls to the CS.
 * <p>
 * Auth: none (internal cluster communication).
 */
public class CallServerClient extends BaseApiClient {

    private static final String TRACK_PATH = "/calls/track";
    private static final String PARTICIPANT_JOINED_PATH = "/calls/%s/participant-joined";
    private static final String SCREENSHOT_PATH = "/calls/%s/screenshot";
    private static final String STATUS_PATH = "/calls/%s/status";
    private static final String HEALTH_PATH = "/health";

    public CallServerClient(String baseUrl) {
        super(baseUrl, null, new HttpClientProperties());
    }

    /**
     * Notifies the Call Server that a new Teams call has started.
     * In the real system, the Recording Bot calls this when it detects a new call.
     */
    public ApiResponse trackCall(CallTrackRequest request) {
        return post(TRACK_PATH, request);
    }

    /**
     * Notifies the Call Server that a participant joined an active call.
     */
    public ApiResponse participantJoined(String callId, Object participantData) {
        return post(String.format(PARTICIPANT_JOINED_PATH, callId), participantData);
    }

    /**
     * Sends a captured screenshot for an external participant to the Call Server.
     */
    public ApiResponse sendScreenshot(String callId, Object screenshotData) {
        return post(String.format(SCREENSHOT_PATH, callId), screenshotData);
    }

    /**
     * Returns the current state of a tracked call.
     */
    public ApiResponse getCallStatus(String callId) {
        return get(String.format(STATUS_PATH, callId));
    }

    /**
     * Health check endpoint.
     */
    public ApiResponse healthCheck() {
        return get(HEALTH_PATH);
    }
}
