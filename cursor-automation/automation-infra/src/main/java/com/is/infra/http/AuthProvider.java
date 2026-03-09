package com.is.infra.http;

import io.restassured.specification.RequestSpecification;

/**
 * Contract for all authentication strategies.
 * Each auth implementation handles its own token/cookie lifecycle.
 */
public interface AuthProvider {

    /**
     * Performs the authentication flow (login request, token capture, etc.).
     * Called once before the first request that requires auth.
     */
    void authenticate();

    /**
     * Applies the captured credentials to an outgoing request.
     *
     * @param spec the RestAssured request specification to enrich
     */
    void applyAuth(RequestSpecification spec);

    /**
     * Returns true if credentials have been obtained and are still valid.
     */
    boolean isAuthenticated();
}
