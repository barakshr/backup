package com.is.infra.testng;

import com.is.infra.testng.setup.SetupAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.ITestResult;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Reads annotations on each test method, delegates to registered SetupAction
 * implementations in order before the test, and in reverse order after.
 *
 * This class is product-agnostic: it only calls SetupAction.appliesTo() / setup() /
 * teardown(). It has no knowledge of @TestSetup, @DeepfakeSetup, or any context holder.
 * Each action is responsible for managing its own layer's context holder.
 *
 * Teardown is wrapped in try/catch per action so one failure does not skip the rest.
 */
public class SetupOrchestrator implements IInvokedMethodListener {

    private static final Logger log = LoggerFactory.getLogger(SetupOrchestrator.class);

    @Override
    public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        if (!invokedMethod.isTestMethod()) return;

        Method method = invokedMethod.getTestMethod().getConstructorOrMethod().getMethod();

        for (SetupAction action : SetupActionRegistry.getActions()) {
            if (action.appliesTo(method)) {
                log.debug("Setup: {}", action.getClass().getSimpleName());
                action.setup(method);
            }
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        if (!invokedMethod.isTestMethod()) return;

        Method method = invokedMethod.getTestMethod().getConstructorOrMethod().getMethod();
        List<SetupAction> reversed = new ArrayList<>(SetupActionRegistry.getActions());
        Collections.reverse(reversed);

        for (SetupAction action : reversed) {
            if (action.appliesTo(method)) {
                try {
                    action.teardown();
                } catch (Exception e) {
                    log.error("Teardown failed for {}: {}", action.getClass().getSimpleName(), e.getMessage(), e);
                }
            }
        }
    }
}
