package com.is.deepfake.testng.context;

/**
 * Get-or-create for the current test's DeepfakeTestContext.
 * Use in actions and tests so one context is shared per test (per thread).
 */
public final class DeepfakeContextUtil {

    private DeepfakeContextUtil() {}

    /**
     * Returns the current test's context, creating and setting it if absent.
     */
    public static DeepfakeTestContext getContext() {
        DeepfakeTestContext ctx = DeepfakeContextHolder.get();
        if (ctx == null) {
            ctx = new DeepfakeTestContext();
            DeepfakeContextHolder.set(ctx);
        }
        return ctx;
    }
}
