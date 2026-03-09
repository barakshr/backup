package com.is.deepfake.clients;

import com.is.deepfake.config.DeepfakeConfig;
import com.is.infra.http.ApiResponse;
import com.is.infra.http.BaseApiClient;

/**
 * Client for the IronScales Notification Bot API.
 * Stub — to be implemented when NB service tests are added (stage 2).
 *
 * The Notification Bot is responsible for delivering in-meeting popup alerts
 * to participants when a deepfake is detected. It joins the Teams meeting
 * and sends messages via Microsoft Graph API.
 *
 * Service test approach:
 *   - Send a trigger request to the NB
 *   - Mock the Microsoft Graph API endpoint with WireMock
 *   - Verify the NB sent the correct request to the mock
 *
 * Auth: TBD (likely Bearer token or internal service auth)
 */
public class NotificationBotClient extends BaseApiClient {

    public NotificationBotClient(DeepfakeConfig config) {
        super(config.getNotificationBotBaseUrl(), null);
    }

    /**
     * Sends a deepfake alert trigger to the Notification Bot.
     * Used in NB service tests to simulate a Call Server notification.
     * TODO: implement when NB endpoint spec is available.
     */
    public ApiResponse triggerAlert(Object alertData) {
        throw new UnsupportedOperationException("NotificationBotClient.triggerAlert() not yet implemented");
    }
}
