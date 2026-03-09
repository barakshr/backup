package com.is.infra.testng;

import com.is.infra.reporting.AllureHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * TestNG listener for cross-cutting concerns: failure capture, cleanup, Allure lifecycle.
 * Stub — to be implemented in stage 2.
 *
 * Wired in via @Listeners on BaseTest.
 *
 * Will handle:
 * - Screenshot on failure (when driver is available)
 * - Allure attachment of request/response on failure
 * - Test data cleanup hooks
 * - Company/tenant deletion after test
 */
public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("TEST FAILED: {} — {}", result.getName(),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "no message");
        // TODO: attach screenshot via AllureHelper.attachScreenshot(...)
        // TODO: attach last request/response via AllureHelper.attachText(...)
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("TEST PASSED: {}", result.getName());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("TEST SKIPPED: {}", result.getName());
    }
}
