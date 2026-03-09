package com.is.deepfake.tests;

import com.is.deepfake.clients.DemoToolClient;
import com.is.deepfake.config.DeepfakeConfig;
import com.is.infra.testng.BaseApiTest;

import java.beans.Transient;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Base class for all Deepfake API tests.
 * Initializes the DemoToolClient (and future DF clients) using DeepfakeConfig.
 *
 * All DF API test classes extend this:
 *   DemoToolLoginTest extends BaseDeepfakeApiTest
 */
public class BaseDeepfakeApiTest extends BaseApiTest {

    protected DemoToolClient demoToolClient;
    protected DeepfakeConfig deepfakeConfig;

    @BeforeClass(alwaysRun = true)
    public void setupDeepfakeClients() {
        deepfakeConfig = new DeepfakeConfig(config);
        demoToolClient = new DemoToolClient(deepfakeConfig);
    }

    @Test
    public void testDemoToolLogin() {}
}
