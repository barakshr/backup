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


}
