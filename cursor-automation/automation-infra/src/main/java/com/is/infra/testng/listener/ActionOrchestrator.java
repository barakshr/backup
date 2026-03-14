package com.is.infra.testng.listener;

import com.is.infra.selenium.DriverHolder;
import com.is.infra.testng.ActionRegistry;
import com.is.infra.testng.action.Action;

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
 * teardown(). After all action teardowns, quits and clears the driver (DriverHolder).
 *
 * Teardown is wrapped in try/catch per action so one failure does not skip the rest.
 */
public class ActionOrchestrator implements IInvokedMethodListener {

    private static final Logger log = LoggerFactory.getLogger(ActionOrchestrator.class);

    @Override
    public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        if (!invokedMethod.isTestMethod()) return;

        Method method = invokedMethod.getTestMethod().getConstructorOrMethod().getMethod();

        for (Action action : ActionRegistry.getActions()) {
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
        List<Action> reversed = new ArrayList<>(ActionRegistry.getActions());
        Collections.reverse(reversed);

        for (Action action : reversed) {
            if (action.appliesTo(method)) {
                try {
                    action.teardown();
                } catch (Exception e) {
                    log.error("Teardown failed for {}: {}", action.getClass().getSimpleName(), e.getMessage(), e);
                }
            }
        }

        DriverHolder.quitAndClear();
    }
}
