package com.is.infra.mock;

/**
 * Manages a WireMock server instance for service-level tests.
 * Stub — to be implemented in stage 2 when NB/CS service tests are added.
 *
 * Intended usage:
 *   MockServerManager mock = new MockServerManager(8089);
 *   mock.start();
 *   mock.stub(post("/teams/graphapi/sendMessage").willReturn(ok()));
 *   // run test
 *   mock.verify(postRequestedFor(urlEqualTo("/teams/graphapi/sendMessage")));
 *   mock.stop();
 *
 * Will be started/stopped by BaseApiTest in @BeforeClass/@AfterClass
 * for tests that require mocking of external dependencies
 * (Microsoft Graph API, Amazon Rekognition, etc.).
 */
public class MockServerManager {

    public MockServerManager(int port) {
        throw new UnsupportedOperationException("MockServerManager not yet implemented");
    }
}
