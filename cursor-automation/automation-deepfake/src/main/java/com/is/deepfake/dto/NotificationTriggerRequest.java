package com.is.deepfake.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Request body for POST /api/v1/notifications/trigger.
 * Sent by Call Server (or test) to the Notification Bot to trigger a deepfake alert.
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class NotificationTriggerRequest {

    private String callId;
    private String tenantId;
    private String meetingLink;
    private String organizerId;
    private List<NotificationParticipant> internalParticipants;
    private DetectedPerson detectedPerson;
    private String threatLevel;
}
