package com.is.infra.mock;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

/**
 * Generic WireMock lifecycle wrapper for service-level tests.
 * Zero product knowledge — any module can use this to mock any external HTTP dependency.
 * <p>
 * Typical lifecycle (managed by ServiceBaseTest or equivalent):
 * <pre>
 *   @BeforeClass  → new MockServerManager(0).start()
 *   @BeforeMethod → mockServer.resetAll()
 *   @AfterClass   → mockServer.stop()
 * </pre>
 */
public class MockServerManager {

    private static final Logger log = LoggerFactory.getLogger(MockServerManager.class);

    private final WireMockServer server;

    /**
     * @param port the port to listen on. Pass 0 to let the OS pick a free port.
     */
    public MockServerManager(int port) {
        this.server = new WireMockServer(wireMockConfig().port(port));
    }

    public void start() {
        server.start();
        WireMock.configureFor("localhost", server.port());
        log.info("WireMock started on port {}", server.port());
    }

    public void stop() {
        if (server.isRunning()) {
            server.stop();
            log.info("WireMock stopped");
        }
    }

    /**
     * Removes all stub mappings and request history. Call between tests for isolation.
     */
    public void resetAll() {
        server.resetAll();
    }

    /**
     * Registers a stub mapping.
     * <pre>
     *   mockServer.stub(post(urlEqualTo("/v1.0/chats/123/messages"))
     *       .willReturn(aResponse().withStatus(201)));
     * </pre>
     */
    public void stub(MappingBuilder mappingBuilder) {
        server.stubFor(mappingBuilder);
    }

    /**
     * Verifies that a request matching the pattern was received exactly {@code count} times.
     */
    public void verify(int count, RequestPatternBuilder pattern) {
        server.verify(count, pattern);
    }

    /**
     * Verifies that a request matching the pattern was received at least once.
     */
    public void verify(RequestPatternBuilder pattern) {
        server.verify(pattern);
    }

    /**
     * Returns the port the mock server is listening on (useful when started with port 0).
     */
    public int getPort() {
        return server.port();
    }

    /**
     * Returns the base URL of the mock server (e.g. {@code http://localhost:8089}).
     */
    public String getBaseUrl() {
        return server.baseUrl();
    }

    public boolean isRunning() {
        return server.isRunning();
    }
}
