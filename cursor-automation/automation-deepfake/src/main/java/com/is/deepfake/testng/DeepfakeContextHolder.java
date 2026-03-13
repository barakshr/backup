package com.is.deepfake.testng;

/**
 * Thread-local store for the current test's DeepfakeTestContext.
 *
 * Each thread (test method in parallel execution) has its own isolated DeepfakeTestContext.
 * CreateDfsTenantAction sets the context in setup() and clears it in teardown().
 *
 * Usage in tests:
 *   Object tenant = DeepfakeContextHolder.get().getDfsTenant();
 */
public class DeepfakeContextHolder {

    private static final ThreadLocal<DeepfakeTestContext> CONTEXT = new ThreadLocal<>();

    private DeepfakeContextHolder() {}

    public static DeepfakeTestContext get() {
        return CONTEXT.get();
    }

    public static void set(DeepfakeTestContext context) {
        CONTEXT.set(context);
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
