package com.is.deepfake.mock;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/**
 * Pre-built WireMock stub definitions for the Microsoft Graph API.
 * Product-specific — knows about Graph API paths and payloads.
 * <p>
 * Used with {@link com.is.infra.mock.MockServerManager}:
 * <pre>
 *   mockServer.stub(GraphApiStubs.sendMessageSuccess());
 *   // ... trigger NB ...
 *   mockServer.verify(1, GraphApiStubs.sendMessageWasCalled());
 * </pre>
 */
public final class GraphApiStubs {

    private static final String SEND_MESSAGE_PATH = "/v1.0/chats/.*/messages";
    private static final String SEND_MESSAGE_URL_PATTERN = "/v1.0/chats/.*/messages";

    private GraphApiStubs() {}

    /**
     * Graph API returns 201 Created.
     * The stub only matches requests whose body contains {@code correlationId}
     * (the unique callId for this test), so parallel tests don't collide.
     */
    public static MappingBuilder sendMessageSuccess(String correlationId) {
        return post(urlMatching(SEND_MESSAGE_PATH))
                .withRequestBody(containing(correlationId))
                .willReturn(aResponse()
                        .withStatus(201)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"id\": \"msg-001\", \"createdDateTime\": \"2026-04-02T12:00:00Z\"}"));
    }

    /**
     * Graph API returns 401 Unauthorized (expired/invalid token).
     * The stub only matches requests whose body contains {@code correlationId}.
     */
    public static MappingBuilder sendMessageUnauthorized(String correlationId) {
        return post(urlMatching(SEND_MESSAGE_PATH))
                .withRequestBody(containing(correlationId))
                .willReturn(aResponse()
                        .withStatus(401)
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\"error\": {\"code\": \"InvalidAuthenticationToken\", " +
                                "\"message\": \"Access token has expired or is not yet valid.\"}}"));
    }

    /**
     * Verification pattern: a POST was made to the Graph API send-message endpoint.
     */
    public static RequestPatternBuilder sendMessageWasCalled() {
        return postRequestedFor(urlMatching(SEND_MESSAGE_URL_PATTERN));
    }

    /**
     * Parallel-safe verification: matches only Graph requests whose body contains the
     * given correlation ID (typically the unique callId generated per test).
     */
    public static RequestPatternBuilder sendMessageWasCalledWithCorrelationId(String correlationId) {
        return postRequestedFor(urlMatching(SEND_MESSAGE_URL_PATTERN))
                .withRequestBody(containing(correlationId));
    }

    /**
     * Same as {@link #sendMessageWasCalled()}, plus the request body must contain the given substring.
     */
    public static RequestPatternBuilder sendMessageWasCalledWithBodyContaining(String substring) {
        return postRequestedFor(urlMatching(SEND_MESSAGE_URL_PATTERN))
                .withRequestBody(containing(substring));
    }
}
