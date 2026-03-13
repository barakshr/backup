package com.is.infra.testng;

import com.is.infra.reporting.AllureHelper;
import com.is.infra.selenium.DriverHolder;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestListener;
import org.testng.ITestResult;

/**
 * Cross-cutting test lifecycle observer.
 * On failure: attach a screenshot to Allure if a driver was used (DriverHolder).
 */
public class TestListener implements ITestListener {

    private static final Logger log = LoggerFactory.getLogger(TestListener.class);

    @Override
    public void onTestStart(ITestResult result) {
        log.info("TEST START: {}.{}", result.getTestClass().getName(), result.getName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("TEST PASSED: {}", result.getName());
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("TEST FAILED: {} — {}",
                result.getName(),
                result.getThrowable() != null ? result.getThrowable().getMessage() : "no message");
        attachScreenshotIfAvailable(result);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("TEST SKIPPED: {}", result.getName());
    }

    private void attachScreenshotIfAvailable(ITestResult result) {
        try {
            if (!DriverHolder.hasDriver()) return;

            WebDriver driver = DriverHolder.getDriver();
            if (driver instanceof TakesScreenshot ts) {
                byte[] screenshot = ts.getScreenshotAs(OutputType.BYTES);
                AllureHelper.attachScreenshot("Failure screenshot — " + result.getName(), screenshot);
                log.info("Screenshot attached for failed test: {}", result.getName());
            }
        } catch (Exception e) {
            log.warn("Could not attach screenshot for {}: {}", result.getName(), e.getMessage());
        }
    }
}
