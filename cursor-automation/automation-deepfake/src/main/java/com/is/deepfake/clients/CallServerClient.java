package com.is.deepfake.clients;

import com.is.deepfake.config.DeepfakeConfig;
import com.is.infra.http.ApiResponse;
import com.is.infra.http.BaseApiClient;
import com.is.infra.http.BearerTokenAuthProvider;

/**
 * Client for the IronScales Call Server API.
 * Stub — to be implemented when CS service tests are added.
 *
 * The Call Server is the brain of the DFS system. It receives:
 * - Call metadata from the Recording Bot
 * - Image streams for AI-based deepfake analysis
 *
 * And produces:
 * - Record/skip decisions per participant
 * - Deepfake verdicts (written to DB)
 * - Notification triggers to the Notification Bot
 *
 * Auth: TBD (likely Bearer token or internal service auth)
 */
public class CallServerClient extends BaseApiClient {

    public CallServerClient(DeepfakeConfig config) {
        super(config.getCallServerBaseUrl(), null);
    }

    /**
     * Sends participant metadata to the Call Server.
     * Used in CS service tests to simulate Recording Bot input.
     * TODO: implement when CS endpoint spec is available.
     */
    public ApiResponse sendParticipantData(Object participantData) {
        throw new UnsupportedOperationException("CallServerClient.sendParticipantData() not yet implemented");
    }

    /**
     * Returns the Call Server's decision for a given participant.
     * TODO: implement
     */
    public ApiResponse getParticipantDecision(String callId, String participantId) {
        throw new UnsupportedOperationException("CallServerClient.getParticipantDecision() not yet implemented");
    }
}
