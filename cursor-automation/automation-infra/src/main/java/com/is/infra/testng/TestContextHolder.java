package com.is.infra.testng;

/**
 * Thread-local store for the current test's TestContext.
 *
 * Each thread (test method in parallel execution) has its own isolated TestContext.
 * SetupOrchestrator sets the context in beforeInvocation and clears it in afterInvocation.
 * Tests and Page Objects read from it via get().
 *
 * Usage:
 *   TestContext ctx = TestContextHolder.get();
 *   WebDriver driver = ctx.getDriver();
 */
public class TestContextHolder {

    private static final ThreadLocal<TestContext> CONTEXT = new ThreadLocal<>();

    private TestContextHolder() {}

    public static TestContext get() {
        return CONTEXT.get();
    }

    public static void set(TestContext context) {
        CONTEXT.set(context);
    }

    public static void clear() {
        CONTEXT.remove();
    }
}
