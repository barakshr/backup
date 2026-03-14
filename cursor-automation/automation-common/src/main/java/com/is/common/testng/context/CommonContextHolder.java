package com.is.common.testng.context;

/**
 * Thread-local store for the current test's CommonTestContext.
 *
 * Each thread (test method in parallel execution) has its own isolated CommonTestContext.
 * CreateCompanyAction sets the context in setup() and clears it in teardown().
 *
 * Usage in tests:
 *   CompanyDto company = CommonContextHolder.get().getCompany();
 */
public class CommonContextHolder {

    private static final ThreadLocal<CommonTestContext> CONTEXT = new ThreadLocal<>();

    private CommonContextHolder() {}

    public static CommonTestContext get() {
        return CONTEXT.get();
    }

    public static void set(CommonTestContext context) {
        CONTEXT.set(context);
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
