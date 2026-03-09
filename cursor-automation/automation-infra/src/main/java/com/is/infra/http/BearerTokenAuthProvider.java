package com.is.infra.http;

import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * AuthProvider for services that use a Bearer token in the Authorization header.
 * Stub — to be implemented when a Bearer-based service is added.
 */
public class BearerTokenAuthProvider implements AuthProvider {

    private static final Logger log = LoggerFactory.getLogger(BearerTokenAuthProvider.class);

    private final String tokenUrl;
    private final String clientId;
    private final String clientSecret;

    private String token;

    public BearerTokenAuthProvider(String tokenUrl, String clientId, String clientSecret) {
        this.tokenUrl = tokenUrl;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
    }

    @Override
    public void authenticate() {
        // TODO: implement token acquisition (e.g. client_credentials OAuth2 flow)
        throw new UnsupportedOperationException("BearerTokenAuthProvider.authenticate() not yet implemented");
    }

    @Override
    public void applyAuth(RequestSpecification spec) {
        if (!isAuthenticated()) {
            authenticate();
        }
        spec.header("Authorization", "Bearer " + token);
    }

    @Override
    public boolean isAuthenticated() {
        return token != null && !token.isBlank();
    }
}
