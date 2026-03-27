package com.is.infra.http;

import java.time.Duration;
import java.util.function.Supplier;

import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;
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
    private final HttpClientProperties httpProps;

    /**
     * Primary constructor. {@link HttpClientProperties} is built from {@code http.*} config keys.
     */
    protected BaseApiClient(String baseUrl, AuthProvider authProvider, HttpClientProperties httpProps) {
        this.baseUrl = baseUrl;
        this.authProvider = authProvider;
        this.httpProps = httpProps;
    }

    /**
     * Constructor for stub clients using default {@link HttpClientProperties} (reads {@link com.is.infra.config.ConfigManager}).
     */
    protected BaseApiClient(String baseUrl, AuthProvider authProvider) {
        this(baseUrl, authProvider, new HttpClientProperties());
    }

    /**
     * Builds a base request with auth applied, base URL set, and logging enabled.
     * Every domain method should start with this.
     */
    protected RequestSpecification baseRequest() {
        RequestSpecification spec = RestAssured
                .given()
                .baseUri(baseUrl)
                .config(restAssuredConfig());

        if (httpProps.isLogRequests()) {
            spec.filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter());
        }

        if (authProvider != null) {
            authProvider.applyAuth(spec);
        }

        return spec;
    }

    /**
     * Builds a request without auth. Used for public endpoints (e.g. health check, login).
     */
    protected RequestSpecification unauthenticatedRequest() {
        RequestSpecification spec = RestAssured
                .given()
                .baseUri(baseUrl)
                .config(restAssuredConfig());

        if (httpProps.isLogRequests()) {
            spec.filter(new RequestLoggingFilter())
                .filter(new ResponseLoggingFilter());
        }

        return spec;
    }

    private RestAssuredConfig restAssuredConfig() {
        return RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout", httpProps.getReadTimeout() * 1000)
                        .setParam("http.connection.timeout", httpProps.getConnectionTimeout() * 1000));
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

    // ─── polling overloads ───

    /**
     * Polls GET {path} until the raw {@link ApiResponse} matches the condition.
     */
    protected ApiResponse get(String path, PollingCondition<ApiResponse> condition) {
        return pollRaw(() -> get(path), condition, "GET " + path);
    }

    /**
     * Polls GET {path}, deserializes to {@code type}, and checks the typed condition.
     */
    protected <T> TypedApiResponse<T> getUntil(String path, Class<T> type, PollingCondition<T> condition) {
        return pollTyped(() -> get(path), type, condition, "GET " + path);
    }

    /**
     * Polls POST {path} until the raw {@link ApiResponse} matches the condition.
     */
    protected ApiResponse post(String path, Object body, PollingCondition<ApiResponse> condition) {
        return pollRaw(() -> post(path, body), condition, "POST " + path);
    }

    /**
     * Polls POST {path}, deserializes to {@code type}, and checks the typed condition.
     */
    protected <T> TypedApiResponse<T> postUntil(String path, Object body, Class<T> type, PollingCondition<T> condition) {
        return pollTyped(() -> post(path, body), type, condition, "POST " + path);
    }

    // ─── polling engine ───

    private ApiResponse pollRaw(
            Supplier<ApiResponse> call,
            PollingCondition<ApiResponse> condition,
            String description) {

        long deadline = System.currentTimeMillis() + condition.getMaxWait().toMillis();
        ApiResponse last = null;
        Exception lastException = null;
        int attempt = 0;

        while (System.currentTimeMillis() < deadline) {
            attempt++;
            try {
                last = call.get();
                log.debug("Poll attempt {} for [{}] — status={}", attempt, description, last.getStatusCode());

                if (condition.getFailWhen() != null && condition.getFailWhen().test(last)) {
                    throw new PollingTimeoutException(
                            String.format("Fail-fast triggered on attempt %d for [%s]. Last status=%d, body=%s",
                                    attempt, description, last.getStatusCode(), truncate(last.getBody())),
                            last);
                }

                if (condition.getUntil().test(last)) {
                    log.debug("Poll condition met on attempt {} for [{}]", attempt, description);
                    return last;
                }
            } catch (PollingTimeoutException e) {
                throw e;
            } catch (Exception e) {
                lastException = e;
                log.debug("Poll attempt {} for [{}] threw: {}", attempt, description, e.getMessage());
                if (!condition.isIgnoreExceptions()) {
                    throw new PollingTimeoutException(
                            String.format("Exception on attempt %d for [%s]: %s",
                                    attempt, description, e.getMessage()),
                            last, e);
                }
            }

            sleep(condition.getPollInterval());
        }

        String lastInfo = last != null
                ? String.format("status=%d, body=%s", last.getStatusCode(), truncate(last.getBody()))
                : "no successful response";
        throw new PollingTimeoutException(
                String.format("Condition not met within %s for [%s] after %d attempts. Last: %s",
                        condition.getMaxWait(), description, attempt, lastInfo),
                last, lastException);
    }

    private <T> TypedApiResponse<T> pollTyped(
            Supplier<ApiResponse> call,
            Class<T> type,
            PollingCondition<T> condition,
            String description) {

        long deadline = System.currentTimeMillis() + condition.getMaxWait().toMillis();
        ApiResponse lastRaw = null;
        T lastObj = null;
        Exception lastException = null;
        int attempt = 0;

        while (System.currentTimeMillis() < deadline) {
            attempt++;
            try {
                lastRaw = call.get();
                lastObj = lastRaw.as(type);
                log.debug("Poll attempt {} for [{}] — deserialized {}", attempt, description, type.getSimpleName());

                if (condition.getFailWhen() != null && condition.getFailWhen().test(lastObj)) {
                    throw new PollingTimeoutException(
                            String.format("Fail-fast triggered on attempt %d for [%s]. Last value: %s",
                                    attempt, description, lastObj),
                            lastObj);
                }

                if (condition.getUntil().test(lastObj)) {
                    log.debug("Poll condition met on attempt {} for [{}]", attempt, description);
                    return new TypedApiResponse<>(lastRaw, type);
                }
            } catch (PollingTimeoutException e) {
                throw e;
            } catch (Exception e) {
                lastException = e;
                log.debug("Poll attempt {} for [{}] threw: {}", attempt, description, e.getMessage());
                if (!condition.isIgnoreExceptions()) {
                    throw new PollingTimeoutException(
                            String.format("Exception on attempt %d for [%s]: %s",
                                    attempt, description, e.getMessage()),
                            lastObj, e);
                }
            }

            sleep(condition.getPollInterval());
        }

        String lastInfo = lastObj != null ? lastObj.toString() : "no successful response";
        throw new PollingTimeoutException(
                String.format("Condition not met within %s for [%s] after %d attempts. Last: %s",
                        condition.getMaxWait(), description, attempt, lastInfo),
                lastObj, lastException);
    }

    private void sleep(Duration duration) {
        try {
            Thread.sleep(duration.toMillis());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Polling interrupted", e);
        }
    }

    private String truncate(String s) {
        if (s == null) return "null";
        return s.length() > 200 ? s.substring(0, 200) + "..." : s;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
