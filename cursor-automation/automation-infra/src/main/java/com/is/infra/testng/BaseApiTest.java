package com.is.infra.testng;

/**
 * Base class for all API tests.
 * Extends BaseTest and adds API-specific lifecycle hooks.
 *
 * Product API test base classes extend this:
 *   BaseDeepfakeApiTest extends BaseApiTest
 *   BaseEmailSecurityApiTest extends BaseApiTest
 */
public class BaseApiTest extends BaseTest {

    // API-specific before/after hooks go here as capabilities are added.
    // e.g.:
    //   @BeforeClass — spin up WireMock server for service tests
    //   @AfterClass  — shut down WireMock, clean up test data
}
