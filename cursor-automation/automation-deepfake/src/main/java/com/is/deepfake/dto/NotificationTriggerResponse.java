package com.is.deepfake.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Response from POST /api/v1/notifications/trigger.
 * Returned by the Notification Bot after processing the alert.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationTriggerResponse {

    private String notificationId;
    private String status;
    private int recipientCount;
    private String timestamp;
}
