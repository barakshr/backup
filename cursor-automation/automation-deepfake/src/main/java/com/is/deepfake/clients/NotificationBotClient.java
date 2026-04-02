package com.is.deepfake.clients;

import com.is.deepfake.config.DeepFakeConfig;
import com.is.deepfake.dto.NotificationTriggerRequest;
import com.is.deepfake.dto.NotificationTriggerResponse;
import com.is.infra.http.ApiResponse;
import com.is.infra.http.BaseApiClient;
import com.is.infra.http.HttpClientProperties;
import com.is.infra.http.TypedApiResponse;

/**
 * Client for the Notification Bot API.
 * <p>
 * The Notification Bot receives deepfake alert triggers from the Call Server
 * and delivers popup notifications to Teams participants via Microsoft Graph API.
 * <p>
 * Auth: none (internal cluster communication).
 * Config: {@code notification.bot.base.url} via {@link DeepFakeConfig}.
 */
public class NotificationBotClient extends BaseApiClient {

    private static final String TRIGGER_PATH = "/api/v1/notifications/trigger";
    private static final String HEALTH_PATH = "/health";

    public NotificationBotClient() {
        super(
                DeepFakeConfig.get().getNotificationBotBaseUrl(),
                null,
                new HttpClientProperties());
    }

    /**
     * Constructor that accepts an explicit base URL (useful for tests pointing to WireMock).
     */
    public NotificationBotClient(String baseUrl) {
        super(baseUrl, null, new HttpClientProperties());
    }

    /**
     * Sends a deepfake alert trigger to the Notification Bot.
     *
     * @param request the alert details (call, participants, detected person)
     * @return raw API response
     */
    public ApiResponse triggerAlert(NotificationTriggerRequest request) {
        return post(TRIGGER_PATH, request);
    }

    /**
     * Sends a trigger and deserializes the response.
     */
    public TypedApiResponse<NotificationTriggerResponse> triggerAlertTyped(NotificationTriggerRequest request) {
        return new TypedApiResponse<>(post(TRIGGER_PATH, request), NotificationTriggerResponse.class);
    }

    /**
     * Health check endpoint.
     */
    public ApiResponse healthCheck() {
        return get(HEALTH_PATH);
    }
}
