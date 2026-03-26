package com.is.deepfake.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Response DTO for GET /calls/status.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class CallStatusResponse {

    @JsonProperty("password")
    private String password;


    public String getPassword() {
        return password;
    }
}