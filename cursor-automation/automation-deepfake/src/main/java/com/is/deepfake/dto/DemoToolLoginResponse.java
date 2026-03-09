package com.is.deepfake.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * Represents the DemoTool authentication response.
 * The DemoTool uses cookie-based auth — after a successful POST /auth/login,
 * the JWT is returned in a Set-Cookie header (not in the response body).
 *
 * This DTO captures any body data returned alongside the redirect.
 * Annotated with @JsonIgnoreProperties to tolerate API changes gracefully.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class DemoToolLoginResponse {

    // DemoTool returns a 303 redirect on success.
    // The actual session token is in the Set-Cookie header, captured by CookieAuthProvider.
    // Fields here represent any body content returned with the response.

    private String message;
    private String status;
}
