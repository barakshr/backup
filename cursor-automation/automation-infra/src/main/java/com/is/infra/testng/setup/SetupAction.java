package com.is.infra.testng.setup;

import java.lang.reflect.Method;

/**
 * Contract for all pre-test setup and post-test teardown actions.
 *
 * Each implementation handles exactly one concern (browser, company, DFS tenant, etc.)
 * and owns its own layer's context holder (CommonContextHolder, DeepfakeContextHolder, etc.).
 * Actions are registered in SetupActionRegistry and executed by SetupOrchestrator.
 *
 * Key design decisions:
 *   - appliesTo(Method) receives the raw test Method — the action reads whatever
 *     annotation it needs (@TestSetup, @DeepfakeSetup, etc.). Infra does not dictate
 *     which annotation a product action reads.
 *   - setup() writes to the action's own layer context holder (not a shared builder).
 *   - teardown() reads from the same holder, cleans up, then clears the holder.
 *   - teardown() is always called for actions that applied, even on test failure.
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
     * Provisions a resource and stores it in this action's own layer context holder.
     * Called before the test method runs, on the same thread as the test.
     */
    void setup(Method testMethod);

    /**
     * Releases the resource and clears this action's own layer context holder.
     * Called after the test method finishes — always, even on failure.
     */
    void teardown();
}
