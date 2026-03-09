package com.is.infra.http;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AuthProvider for services that authenticate via a login endpoint and
 * return a session token/JWT inside a Set-Cookie header on a redirect response.
 *
 * Flow:
 *   1. POST login endpoint with form credentials
 *   2. Server responds with 303 redirect + Set-Cookie containing the JWT
 *   3. Capture cookie from redirect response (auto-redirect disabled)
 *   4. Apply cookie as Cookie header on all subsequent requests
 */
public class CookieAuthProvider implements AuthProvider {

    private static final Logger log = LoggerFactory.getLogger(CookieAuthProvider.class);

    private final String loginUrl;
    private final String username;
    private final String password;

    private String capturedCookie;

    public CookieAuthProvider(String loginUrl, String username, String password) {
        this.loginUrl = loginUrl;
        this.username = username;
        this.password = password;
    }

    @Override
    public void authenticate() {
        log.info("Authenticating via cookie auth: POST {}", loginUrl);

        Response response = RestAssured
                .given()
                    .redirects().follow(false)
                    .contentType("application/x-www-form-urlencoded")
                    .formParam("username", username)
                    .formParam("password", password)
                .when()
                    .post(loginUrl);

        log.info("Auth response status: {}", response.getStatusCode());

        capturedCookie = extractCookie(response);

        if (capturedCookie == null || capturedCookie.isBlank()) {
            throw new ApiException(
                    "POST", loginUrl, response.getStatusCode(),
                    "Authentication failed: no Set-Cookie header found in response. Body: " + response.getBody().asString()
            );
        }

        log.info("Authentication successful. Cookie captured.");
    }

    @Override
    public void applyAuth(RequestSpecification spec) {
        if (!isAuthenticated()) {
            authenticate();
        }
        spec.header("Cookie", capturedCookie);
    }

    @Override
    public boolean isAuthenticated() {
        return capturedCookie != null && !capturedCookie.isBlank();
    }

    /**
     * Extracts the raw Set-Cookie value from the response.
     * Tries the redirect response Set-Cookie header first.
     */
    private String extractCookie(Response response) {
        String setCookie = response.getHeader("Set-Cookie");
        if (setCookie != null && !setCookie.isBlank()) {
            // Return the full cookie string (e.g. "session=abc123; Path=/; HttpOnly")
            // Strip directives — keep only name=value
            return setCookie.split(";")[0].trim();
        }
        return null;
    }
}
