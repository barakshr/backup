package com.is.infra.testng;

import com.is.infra.selenium.BrowserType;
import com.is.infra.selenium.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Holds browser/driver resources for a single test method.
 * Stored in TestContextHolder (ThreadLocal) — one instance per thread, parallel-safe.
 * Lifecycle managed exclusively by StartBrowserAction.
 *
 * Infra concerns only — no company, no product-specific data.
 * Product-specific data lives in its own layer's context:
 *   - CompanyDto   → CommonTestContext  (automation-common)
 *   - DfsTenant    → DeepfakeTestContext (automation-deepfake)
 *
 * Driver is created lazily on the first call to getDriver(), which happens
 * when a Page Object is first constructed in the test body.
 */
public class TestContext {

    private static final Logger log = LoggerFactory.getLogger(TestContext.class);

    private final BrowserType browserType;
    private final boolean headless;
    private final AtomicReference<WebDriver> driverRef = new AtomicReference<>();

    public TestContext(BrowserType browserType, boolean headless) {
        this.browserType = browserType;
        this.headless    = headless;
    }

    /**
     * Returns the WebDriver for this test, creating it lazily on first access.
     * Returns null if no browser context was created (non-UI test).
     * Thread-safe via AtomicReference.compareAndSet.
     */
    public WebDriver getDriver() {
        if (browserType == null) return null;
        if (driverRef.get() == null) {
            WebDriver driver = DriverFactory.create(browserType, headless);
            if (!driverRef.compareAndSet(null, driver)) {
                driver.quit();
            }
        }
        return driverRef.get();
    }

    /** Returns true if a WebDriver has been created (i.e. a Page Object was used). */
    public boolean hasDriver() {
        return driverRef.get() != null;
    }

    /** Quits the driver if it was opened. No-op if the test never used the browser. */
    public void quitDriver() {
        WebDriver driver = driverRef.getAndSet(null);
        if (driver != null) {
            log.info("Quitting browser");
            DriverFactory.quit(driver);
        }
    }
}
