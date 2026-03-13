package com.is.infra.testng;

import com.is.infra.selenium.BrowserType;
import com.is.infra.selenium.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Holds all resources provisioned for a single test method.
 * Stored in TestContextHolder (ThreadLocal) — one instance per thread, parallel-safe.
 *
 * Driver lifecycle:
 *   - Browser type and headless flag are recorded at setup time (by StartBrowserAction).
 *   - The actual WebDriver is created lazily on the first call to getDriver().
 *   - This happens when a Page Object is first constructed in the test body.
 *   - quitDriver() is called by StartBrowserAction.teardown() after the test, always.
 *
 * Company type:
 *   - Stored as Object to keep infra fully product-agnostic.
 *   - Product code (e.g. automation-common) casts to CompanyDto.
 *
 * Product-specific data (e.g. DFS tenant):
 *   - Stored in the extras map via withExtra(key, value). Infra does not define keys.
 *   - Deepfake: CreateDfsTenantAction uses key "dfsTenant"; tests get via getExtra("dfsTenant").
 *
 * See issuesBacklog.md: lazy driver initialization is the intentional side effect
 * on this otherwise-immutable object.
 */
public class TestContext {

    private static final Logger log = LoggerFactory.getLogger(TestContext.class);

    private final Object company;
    private final BrowserType browserType;
    private final boolean headless;
    private final Map<String, Object> extras;
    private final AtomicReference<WebDriver> driverRef = new AtomicReference<>();

    private TestContext(Builder builder) {
        this.company     = builder.company;
        this.browserType = builder.browserType;
        this.headless    = builder.headless;
        this.extras      = builder.extras == null || builder.extras.isEmpty()
                ? Collections.emptyMap()
                : Collections.unmodifiableMap(new HashMap<>(builder.extras));
    }

    /** Returns the company provisioned for this test, or null if none was requested. */
    public Object getCompany() {
        return company;
    }

    /**
     * Returns a product-specific value stored during setup.
     * Keys are defined by product modules (e.g. "dfsTenant" for deepfake).
     * Returns null if the key was not set.
     */
    public Object getExtra(String key) {
        return extras.get(key);
    }

    /** Returns true if a value was stored for the given key. */
    public boolean hasExtra(String key) {
        return extras.containsKey(key);
    }

    /**
     * Returns the WebDriver for this test, creating it lazily on first access.
     * Returns null if @TestSetup(requiresBrowser=false) — no browser was requested.
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

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private Object company;
        private BrowserType browserType;
        private boolean headless = true;
        private Map<String, Object> extras;

        public Builder withCompany(Object company) {
            this.company = company;
            return this;
        }

        public Builder withBrowser(BrowserType type, boolean headless) {
            this.browserType = type;
            this.headless    = headless;
            return this;
        }

        /** Stores a product-specific value for the test. Keys are defined by product modules (e.g. "dfsTenant"). */
        public Builder withExtra(String key, Object value) {
            if (extras == null) extras = new HashMap<>();
            extras.put(key, value);
            return this;
        }

        public TestContext build() {
            return new TestContext(this);
        }
    }
}
