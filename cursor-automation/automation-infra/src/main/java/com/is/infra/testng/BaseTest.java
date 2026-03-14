package com.is.infra.testng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Listeners;

import com.is.infra.testng.listener.SetupOrchestrator;
import com.is.infra.testng.listener.SuiteListener;
import com.is.infra.testng.listener.TestListener;

/**
 * Root base class for all tests across all modules.
 * Integrates Spring context management via AbstractTestNGSpringContextTests.
 *
 * Registered listeners:
 *   - TestListener      — pass/fail/skip logging + screenshot on failure
 *   - SetupOrchestrator — pre-test setup and post-test teardown via SetupActions; driver cleanup after every test
 *   - SuiteListener     — suite start/finish logging
 *
 * Product base tests register product-specific actions in their own @BeforeSuite.
 * Driver is created lazily when a Page Object is first used; no browser action.
 */
@Listeners({TestListener.class, SetupOrchestrator.class, SuiteListener.class})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @AfterSuite(alwaysRun = true)
    public void globalTeardown() {
        log.info("=== Suite finished ===");
    }
}
