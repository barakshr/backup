package com.is.deepfake.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Response from POST /calls/track.
 * Returned by the Call Server after registering a new call.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallTrackResponse {

    private String callId;
    private String status;
}
