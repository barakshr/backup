package com.is.infra.testng;

import com.is.infra.config.ConfigManager;
import com.is.infra.testng.setup.StartBrowserAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Listeners;

/**
 * Root base class for all tests across all modules.
 * Integrates Spring context management via AbstractTestNGSpringContextTests.
 *
 * Registered listeners (applied to every subclass automatically):
 *   - TestListener      — pass/fail/skip logging + screenshot on failure
 *   - SetupOrchestrator — pre-test setup and post-test teardown via SetupActions
 *   - SuiteListener     — suite start/finish logging and extension point
 *
 * Infra-level SetupActions (available to all tests):
 *   - StartBrowserAction — activated by @TestSetup(requiresBrowser=true)
 *
 * Product base tests register product-specific actions in their own @BeforeSuite:
 *   @BeforeSuite(alwaysRun = true)
 *   public void registerProductActions() {
 *       SetupActionRegistry.register(new CreateCompanyAction(companyWorkflow));
 *   }
 *
 * Hierarchy:
 *   BaseTest (infra)
 *     └── BaseApiTest (infra)
 *           └── DeepfakeBaseTest (deepfake) ← @SpringBootTest lives here
 *                 └── DemoToolLoginTest (deepfake)
 */
@Listeners({TestListener.class, SetupOrchestrator.class, SuiteListener.class})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @BeforeSuite(alwaysRun = true)
    public void registerInfraActions() {
        SetupActionRegistry.register(new StartBrowserAction(ConfigManager.load()));
    }

    @AfterSuite(alwaysRun = true)
    public void globalTeardown() {
        log.info("=== Suite finished ===");
    }
}
