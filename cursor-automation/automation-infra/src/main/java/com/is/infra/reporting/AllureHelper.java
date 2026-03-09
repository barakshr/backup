package com.is.infra.reporting;

import io.qameta.allure.Allure;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Utility for attaching test artifacts to Allure reports.
 * Stub — to be wired into TestListener and BaseApiClient in stage 2.
 *
 * Intended usage:
 *   AllureHelper.attachResponse("Login Response", response);
 *   AllureHelper.attachText("Test Data", someJson);
 */
public class AllureHelper {

    private static final Logger log = LoggerFactory.getLogger(AllureHelper.class);

    private AllureHelper() {}

    /**
     * Attaches a plain text string to the current Allure test.
     */
    public static void attachText(String name, String content) {
        try {
            Allure.addAttachment(name, "text/plain",
                    new ByteArrayInputStream(content.getBytes(StandardCharsets.UTF_8)), ".txt");
        } catch (Exception e) {
            log.warn("Failed to attach text to Allure: {}", e.getMessage());
        }
    }

    /**
     * Attaches a JSON string to the current Allure test.
     */
    public static void attachJson(String name, String json) {
        try {
            Allure.addAttachment(name, "application/json",
                    new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)), ".json");
        } catch (Exception e) {
            log.warn("Failed to attach JSON to Allure: {}", e.getMessage());
        }
    }

    /**
     * Attaches an HTTP response body and status to Allure.
     * TODO: wire into BaseApiClient response handling in stage 2.
     */
    public static void attachResponse(String name, Response response) {
        if (response == null) return;
        String content = String.format("Status: %d\n\n%s", response.getStatusCode(), response.getBody().asString());
        attachText(name, content);
    }

    /**
     * Attaches a screenshot (byte array) to Allure.
     * Used by TestListener on test failure.
     * TODO: implement in stage 2 when Selenium is active.
     */
    public static void attachScreenshot(String name, byte[] screenshotBytes) {
        try {
            Allure.addAttachment(name, "image/png",
                    new ByteArrayInputStream(screenshotBytes), ".png");
        } catch (Exception e) {
            log.warn("Failed to attach screenshot to Allure: {}", e.getMessage());
        }
    }
}
