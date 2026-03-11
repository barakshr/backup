package com.is.infra.testng;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.Listeners;

/**
 * Root base class for all tests across all modules.
 * Integrates Spring context management via AbstractTestNGSpringContextTests.
 *
 * The @SpringBootTest annotation and application class are declared in each
 * product module's base test (e.g. DeepfakeBaseTest), keeping infra decoupled
 * from product-specific Spring configurations.
 *
 * Hierarchy:
 *   BaseTest (infra)
 *     └── BaseApiTest (infra)
 *           └── DeepfakeBaseTest (deepfake) ← @SpringBootTest lives here
 *                 └── DemoToolLoginTest (deepfake)
 */
@Listeners({TestListener.class})
public abstract class BaseTest extends AbstractTestNGSpringContextTests {

    private static final Logger log = LoggerFactory.getLogger(BaseTest.class);

    @AfterSuite(alwaysRun = true)
    public void globalTeardown() {
        log.info("=== Suite finished ===");
    }
}
