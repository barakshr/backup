package com.is.infra.testng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Listeners;

import com.is.infra.testng.listener.ActionOrchestrator;
import com.is.infra.testng.listener.SuiteListener;
import com.is.infra.testng.listener.TestListener;

/**
 * Root base class for all tests across all modules.
 * <p>
 * Registered listeners:
 *   - TestListener      — pass/fail/skip logging + screenshot on failure
 *   - ActionOrchestrator — pre-test setup and post-test teardown via Actions; driver cleanup after every test
 *   - SuiteListener     — suite start/finish logging
 * <p>
 * Product base tests register product-specific actions in their own {@code @BeforeSuite}.
 * Driver is created lazily when a Page Object is first used; no browser action.
 */
@Listeners({TestListener.class, ActionOrchestrator.class, SuiteListener.class})
public abstract class BaseTest {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @AfterSuite(alwaysRun = true)
    public void globalTeardown() {
        log.info("=== Suite finished ===");
    }
}
