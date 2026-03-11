package com.is.deepfake.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Configuration properties for the DemoTool service.
 * Bound from the "automation.demo-tool" block in application.yml.
 *
 * Values are supplied via:
 *   - src/test/resources/application.yml (defaults / local dev)
 *   - Environment variables for CI: AUTOMATION_DEMO_TOOL_BASE_URL, etc.
 *
 * Register via @EnableConfigurationProperties(DemoToolProperties.class)
 * in DeepfakeTestApplication.
 */
@Data
@ConfigurationProperties(prefix = "automation.demo-tool")
public class DemoToolProperties {

    private String baseUrl;
    private String username;
    private String password;
}
