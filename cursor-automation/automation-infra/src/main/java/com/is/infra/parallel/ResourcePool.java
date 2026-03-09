package com.is.infra.parallel;

/**
 * Thread-safe pool of shared test resources (e.g. dev accounts, O365 domains).
 * Stub — to be implemented when parallel execution is added (stage 3).
 *
 * Intended usage:
 *   String domain = ResourcePool.acquire(ResourceType.O365_DOMAIN);
 *   try {
 *       // run test using domain
 *   } finally {
 *       ResourcePool.release(domain);
 *   }
 *
 * Internally uses a Semaphore + blocking queue to prevent domain conflicts
 * when multiple tests run in parallel. Resources are pre-configured in
 * config.properties (e.g. o365.domains=wj1b1.onmicrosoft.com,wj1b2.onmicrosoft.com).
 */
public class ResourcePool {

    private ResourcePool() {}

    public static String acquire(String resourceType) {
        throw new UnsupportedOperationException("ResourcePool not yet implemented");
    }

    public static void release(String resource) {
        throw new UnsupportedOperationException("ResourcePool not yet implemented");
    }
}
