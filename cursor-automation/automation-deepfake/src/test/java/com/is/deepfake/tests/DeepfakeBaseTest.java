package com.is.deepfake.tests;

import com.is.deepfake.DeepfakeTestApplication;
import com.is.deepfake.testng.setup.CreateDfsTenantAction;
import com.is.infra.testng.BaseApiTest;
import com.is.infra.testng.SetupActionRegistry;
import org.springframework.boot.test.context.SpringBootTest;
import org.testng.annotations.BeforeSuite;

/**
 * Base class for all Deepfake tests.
 *
 * Bootstraps the Spring context with DeepfakeTestApplication.
 * All deepfake test classes extend this — no manual client initialization needed.
 * Each test class declares only the clients it needs via @Autowired.
 *
 * Deepfake-specific SetupActions are registered in @BeforeSuite below.
 */
@SpringBootTest(classes = DeepfakeTestApplication.class)
public abstract class DeepfakeBaseTest extends BaseApiTest {

    @BeforeSuite(alwaysRun = true)
    public void registerDeepfakeActions() {
        SetupActionRegistry.register(new CreateDfsTenantAction());
    }
}
