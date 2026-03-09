package com.is.infra.http;

import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base class for all API clients. Provides:
 * - RestAssured request building with base URL and auth
 * - Standard HTTP methods (GET, POST, PUT, DELETE, PATCH)
 * - Consistent logging of requests and responses
 *
 * Each domain client (DemoToolClient, CallServerClient, etc.) extends this class
 * and adds service-specific endpoint methods on top.
 */
public abstract class BaseApiClient {

    protected final Logger log = LoggerFactory.getLogger(getClass());

    private final String baseUrl;
    private final AuthProvider authProvider;

    protected BaseApiClient(String baseUrl, AuthProvider authProvider) {
        this.baseUrl = baseUrl;
        this.authProvider = authProvider;
    }

    /**
     * Builds a base request with auth applied, base URL set, and logging enabled.
     * Every domain method should start with this.
     */
    protected RequestSpecification baseRequest() {
        RequestSpecification spec = RestAssured
                .given()
                .baseUri(baseUrl)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter());

        if (authProvider != null) {
            authProvider.applyAuth(spec);
        }

        return spec;
    }

    /**
     * Builds a request without auth. Used for public endpoints (e.g. health check, login).
     */
    protected RequestSpecification unauthenticatedRequest() {
        return RestAssured
                .given()
                .baseUri(baseUrl)
                .filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter());
    }

    protected ApiResponse get(String path) {
        Response response = baseRequest().when().get(path);
        return new ApiResponse(response);
    }

    protected ApiResponse post(String path, Object body) {
        Response response = baseRequest()
                .contentType("application/json")
                .body(body)
                .when()
                .post(path);
        return new ApiResponse(response);
    }

    protected ApiResponse postForm(String path, java.util.Map<String, String> formParams) {
        RequestSpecification spec = unauthenticatedRequest()
                .contentType("application/x-www-form-urlencoded");
        formParams.forEach(spec::formParam);
        Response response = spec.when().post(path);
        return new ApiResponse(response);
    }

    protected ApiResponse put(String path, Object body) {
        Response response = baseRequest()
                .contentType("application/json")
                .body(body)
                .when()
                .put(path);
        return new ApiResponse(response);
    }

    protected ApiResponse delete(String path) {
        Response response = baseRequest().when().delete(path);
        return new ApiResponse(response);
    }

    protected ApiResponse patch(String path, Object body) {
        Response response = baseRequest()
                .contentType("application/json")
                .body(body)
                .when()
                .patch(path);
        return new ApiResponse(response);
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
