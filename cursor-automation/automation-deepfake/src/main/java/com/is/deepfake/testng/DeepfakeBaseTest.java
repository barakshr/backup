package com.is.deepfake.testng;

import com.is.common.testng.CommonBaseTest;
import com.is.deepfake.testng.action.CreateDfsTenantAction;
import com.is.infra.testng.BaseTest;
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
public abstract class DeepfakeBaseTest extends CommonBaseTest {

    @BeforeSuite(alwaysRun = true)
    public void registerDeepfakeActions() {
        SetupActionRegistry.register(new CreateDfsTenantAction());
    }
}
