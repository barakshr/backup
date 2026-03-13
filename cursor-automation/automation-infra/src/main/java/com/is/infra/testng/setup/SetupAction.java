package com.is.infra.testng.setup;

import com.is.infra.testng.TestContext;

import java.lang.reflect.Method;

/**
 * Contract for all pre-test setup and post-test teardown actions.
 *
 * Each implementation handles exactly one concern (browser, company, DB, etc.).
 * Actions are registered in SetupActionRegistry and executed by SetupOrchestrator.
 *
 * Key design decisions:
 *   - appliesTo(Method) receives the raw test Method — the action reads whatever
 *     annotation it needs (@TestSetup, @DeepfakeSetup, etc.). Infra does not dictate
 *     which annotation a product action reads.
 *   - teardown() is always called after the test, even if setup() or the test failed.
 *   - Teardown order is the reverse of setup order (enforced by SetupOrchestrator).
 *
 * Extend AbstractSetupAction for a convenience getAnnotation() helper.
 */
public interface SetupAction {

    /**
     * Returns true if this action should run for the given test method.
     * Implementations typically check for the presence and value of a specific annotation.
     */
    boolean appliesTo(Method testMethod);

    /**
     * Provisions a resource and stores it in the TestContext builder.
     * Called before the test method runs.
     */
    void setup(TestContext.Builder builder, Method testMethod);

    /**
     * Releases or cleans up the resource.
     * Called after the test method finishes — always, even on failure.
     */
    void teardown(TestContext ctx);
}
