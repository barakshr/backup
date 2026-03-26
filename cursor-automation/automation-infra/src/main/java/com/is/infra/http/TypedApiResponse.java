package com.is.infra.http;

import io.restassured.response.Response;

/**
 * A typed wrapper around {@link ApiResponse} that binds a target deserialization class.
 * <p>
 * The method writer bakes in the DTO type at construction time so callers can call
 * {@link #getBodyAsObject()} without passing a class. All standard {@link ApiResponse}
 * operations (status code, headers, raw body) are delegated and remain fully available.
 *
 * @param <T> the target deserialization type
 */
public class TypedApiResponse<T> {

    private final ApiResponse apiResponse;
    private final Class<T> type;

    public TypedApiResponse(ApiResponse apiResponse, Class<T> type) {
        this.apiResponse = apiResponse;
        this.type = type;
    }

    /** Deserializes the response body into {@code T} — no class argument needed at call site. */
    public T getBodyAsObject() {
        return apiResponse.as(type);
    }

    public int getStatusCode()           { return apiResponse.getStatusCode(); }
    public String getBody()              { return apiResponse.getBody(); }
    public String getHeader(String name) { return apiResponse.getHeader(name); }
    public ApiAssert getApiAssert()      { return apiResponse.getApiAssert(); }
    public Response getRawResponse()     { return apiResponse.getRawResponse(); }
}
