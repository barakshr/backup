package com.is.infra.testng;

import org.openqa.selenium.WebDriver;

/**
 * Base class for tests that interact with the UI (Selenium).
 *
 * WebDriver lifecycle is fully managed by StartBrowserAction + SetupOrchestrator:
 *   - Activated by @TestSetup(requiresBrowser=true).
 *   - Driver is created lazily on first access (first Page Object construction).
 *   - Driver is quit by StartBrowserAction.teardown() after the test, always.
 *
 * This class provides getDriver() as a convenience for test subclasses that
 * need the driver directly (outside of a Page Object).
 *
 * No @BeforeMethod or @AfterMethod here — driver lifecycle is not managed here.
 */
public abstract class BaseUiTest extends BaseTest {

    /**
     * Returns the WebDriver for the current test thread.
     * Null if the test did not declare @TestSetup(requiresBrowser=true).
     * Creates the driver lazily on first call if not yet initialized.
     */
    protected WebDriver getDriver() {
        TestContext ctx = TestContextHolder.get();
        if (ctx == null) return null;
        return ctx.getDriver();
    }
}
