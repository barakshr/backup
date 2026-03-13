package com.is.deepfake.testng;

/**
 * Holds deepfake-specific test resources for a single test method.
 * Stored in DeepfakeContextHolder (ThreadLocal) — one instance per thread, parallel-safe.
 * Lifecycle managed by CreateDfsTenantAction.
 *
 * dfsTenant is typed as Object until DfsTenantDto is defined.
 * Once defined, replace with:
 *   private final DfsTenantDto dfsTenant;
 *
 * Retrieve in tests via:
 *   DeepfakeContextHolder.get().getDfsTenant()
 */
public class DeepfakeTestContext {

    private final Object dfsTenant;

    public DeepfakeTestContext(Object dfsTenant) {
        this.dfsTenant = dfsTenant;
    }

    public Object getDfsTenant() {
        return dfsTenant;
    }
}
