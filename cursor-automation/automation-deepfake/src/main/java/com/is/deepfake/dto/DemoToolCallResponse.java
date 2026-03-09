package com.is.deepfake.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response from POST /calls/join-multiple.
 * Stub fields — to be completed once the actual response schema is confirmed.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoToolCallResponse {

    @JsonProperty("call_id")
    private String callId;

    private String status;
    private String message;
}
