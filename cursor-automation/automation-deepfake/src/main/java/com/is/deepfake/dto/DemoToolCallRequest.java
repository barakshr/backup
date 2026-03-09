package com.is.deepfake.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

import java.util.List;

/**
 * Request body for POST /calls/join-multiple.
 * Instructs the DemoTool to join a Teams meeting as a deepfake participant.
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoToolCallRequest {

    @JsonProperty("video_id")
    private String videoId;

    @JsonProperty("meeting_link")
    private String meetingLink;

    @JsonProperty("display_names")
    private List<String> displayNames;

    @JsonProperty("tenant_id")
    private String tenantId;

    @JsonProperty("organizer_user_id")
    private String organizerUserId;

    @JsonProperty("call_stay_time_minutes")
    private int callStayTimeMinutes;
}
