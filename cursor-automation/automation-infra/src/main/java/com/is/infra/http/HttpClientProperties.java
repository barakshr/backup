package com.is.infra.http;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Global HTTP configuration shared by all API clients.
 * Bound from the "automation.http" block in application.yml.
 *
 * Defaults are applied when not overridden in the consuming module's application.yml.
 * Register via @EnableConfigurationProperties(HttpClientProperties.class) in the module's
 * Spring bootstrap class.
 */
@Data
@ConfigurationProperties(prefix = "automation.http")
public class HttpClientProperties {

    /** Socket read timeout in seconds. */
    private int readTimeout = 60;

    /** Connection establishment timeout in seconds. */
    private int connectionTimeout = 30;

    /** Max retry attempts for transient failures (used by RetryAnalyzer). */
    private int retryMaxAttempts = 3;

    /** When true, RestAssured logs full request and response details. */
    private boolean logRequests = true;
}
