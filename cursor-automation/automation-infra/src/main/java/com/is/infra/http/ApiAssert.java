package com.is.infra.http;

import io.restassured.response.Response;

public class ApiAssert {
    private final ApiResponse apiResponse;
    private final int statusCode;
    private final String body;
    private final Response rawResponse;


    public ApiAssert(ApiResponse apiResponse , Response rawResponse) {
        this.apiResponse = apiResponse;
        this.rawResponse = rawResponse;
        this.statusCode = rawResponse.getStatusCode();
        this.body = rawResponse.getBody().asString();
    }

    /**
     * Asserts the expected status code, throwing ApiException if it does not match.
     *
     * @param expected expected HTTP status code
     * @return this, for chaining
     */
    public ApiResponse assertStatusCode(int expected) {
        if (statusCode != expected) {
            throw new ApiException(
                    "ASSERT", "response", statusCode,
                    "Expected status " + expected + " but got " + statusCode + ". Body: " + body
            );
        }
        return apiResponse;
    }
}
