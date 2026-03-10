package com.is.infra.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.response.Response;
import lombok.Getter;

/**
 * Wraps a RestAssured Response providing typed deserialization and
 * convenient accessors. Keeps test code clean — no raw Response handling.
 */
@Getter
public class ApiResponse {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private final Response rawResponse;
    private final int statusCode;
    private final String body;
    private final ApiAssert apiAssert;

    public ApiResponse(Response response) {
        this.rawResponse = response;
        this.statusCode = response.getStatusCode();
        this.body = response.getBody().asString();
        this.apiAssert = new ApiAssert(this, response);
    }

    /**
     * Deserializes the response body into the given type.
     *
     * @param type the target class
     * @param <T>  the return type
     * @return deserialized object
     */
    public <T> T as(Class<T> type) {
        try {
            return MAPPER.readValue(body, type);
        } catch (Exception e) {
            throw new ApiException(
                    "DESERIALIZE", "response body", statusCode,
                    "Failed to deserialize response to " + type.getSimpleName() + ": " + e.getMessage()
            );
        }
    }


    public Response getRawResponse() {
        return rawResponse;
    }
    /**
     * Returns the value of a specific response header.
     */
    public String getHeader(String name) {
        return rawResponse.getHeader(name);
    }

}
