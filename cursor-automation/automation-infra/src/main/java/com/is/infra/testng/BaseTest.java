package com.is.infra.testng;

import com.is.infra.config.ConfigManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

/**
 * Root base class for all tests across all modules.
 * Handles global lifecycle: config loading, suite-level setup and teardown.
 *
 * All test base classes extend this:
 *   BaseApiTest extends BaseTest
 *   BaseUiTest extends BaseTest
 */
@Listeners({TestListener.class})
public class BaseTest {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    protected static ConfigManager config;

    @BeforeSuite(alwaysRun = true)
    public void globalSetup() {
        log.info("=== Suite starting ===");
        config = ConfigManager.load();
        log.info("Config loaded. Environment: {}", config.getString("environment", "not set"));
    }

    @AfterSuite(alwaysRun = true)
    public void globalTeardown() {
        log.info("=== Suite finished ===");
    }
}
