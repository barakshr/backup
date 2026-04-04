package com.is.deepfake.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Request body for POST /calls/track.
 * Sent by the Recording Bot (or test) to the Call Server when a new Teams call starts.
 */
@Data
@Builder
@Jacksonized
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallTrackRequest {

    private String callId;
    private String tenantId;
    private String organizerId;
    private String meetingLink;
}
