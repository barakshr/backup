package com.is.common.testng.context;

/**
 * Get-or-create for the current test's CommonTestContext.
 * Use in actions and tests so one context is shared per test (per thread).
 */
public final class CommonContextUtil {

    private CommonContextUtil() {}

    /**
     * Returns the current test's context, creating and setting it if absent.
     */
    public static CommonTestContext getContext() {
        CommonTestContext ctx = CommonContextHolder.get();
        if (ctx == null) {
            ctx = new CommonTestContext();
            CommonContextHolder.set(ctx);
        }
        return ctx;
    }
}
