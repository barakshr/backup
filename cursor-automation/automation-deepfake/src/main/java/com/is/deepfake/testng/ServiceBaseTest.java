package com.is.deepfake.testng;

import com.is.infra.mock.MockServerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

/**
 * Base class for Deepfake service-level tests that need WireMock.
 * Manages the {@link MockServerManager} lifecycle so individual test classes
 * only contain test methods and service-specific setup.
 * <p>
 * Inheritance chain:
 * BaseTest → CommonBaseTest → DeepfakeBaseTest → ServiceBaseTest → [service tests]
 */
public abstract class ServiceBaseTest extends DeepfakeBaseTest {

    private static final Logger log = LoggerFactory.getLogger(ServiceBaseTest.class);

    protected static MockServerManager mockServer;

    @BeforeClass(alwaysRun = true)
    public void startMockServer() {
        mockServer = new MockServerManager(0);
        mockServer.start();
        log.info("MockServer available at {}", mockServer.getBaseUrl());
    }

    @BeforeMethod(alwaysRun = true)
    public void resetMockServer() {
        mockServer.resetAll();
    }

    @AfterClass(alwaysRun = true)
    public void stopMockServer() {
        if (mockServer != null) {
            mockServer.stop();
        }
    }
}
