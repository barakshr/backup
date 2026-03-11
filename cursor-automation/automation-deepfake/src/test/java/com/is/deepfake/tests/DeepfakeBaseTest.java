package com.is.deepfake.tests;

import com.is.deepfake.DeepfakeTestApplication;
import com.is.infra.testng.BaseApiTest;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base class for all Deepfake tests.
 *
 * Bootstraps the Spring context with DeepfakeTestApplication.
 * All deepfake test classes extend this — no manual client init needed.
 * Each test class declares only the clients it needs via @Autowired.
 *
 * Example:
 *   public class DemoToolLoginTest extends DeepfakeBaseTest {
 *       @Autowired DemoToolClient demoToolClient;
 *   }
 */
@SpringBootTest(classes = DeepfakeTestApplication.class)
public abstract class DeepfakeBaseTest extends BaseApiTest {
}
