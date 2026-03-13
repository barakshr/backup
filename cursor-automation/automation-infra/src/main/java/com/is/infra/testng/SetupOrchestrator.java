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
 * implementations in order, then tears them down in reverse — always, even on failure.
 *
 * Registration: actions are added to SetupActionRegistry in @BeforeSuite methods:
 *   - BaseTest registers infra-level actions (StartBrowserAction).
 *   - Product base tests register product-specific actions (CreateCompanyAction, etc.).
 *
 * This class is product-agnostic: it only calls SetupAction.appliesTo() / setup() /
 * teardown(). It has no knowledge of @TestSetup, @DeepfakeSetup, or any annotation.
 *
 * Teardown is wrapped in try/catch per action so one failure does not skip the rest.
 */
public class SetupOrchestrator implements IInvokedMethodListener {

    private static final Logger log = LoggerFactory.getLogger(SetupOrchestrator.class);

    @Override
    public void beforeInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        if (!invokedMethod.isTestMethod()) return;

        Method method  = invokedMethod.getTestMethod().getConstructorOrMethod().getMethod();
        List<SetupAction> actions = SetupActionRegistry.getActions();
        TestContext.Builder builder = TestContext.builder();

        for (SetupAction action : actions) {
            if (action.appliesTo(method)) {
                log.debug("Setup: {}", action.getClass().getSimpleName());
                action.setup(builder, method);
            }
        }

        TestContextHolder.set(builder.build());
    }

    @Override
    public void afterInvocation(IInvokedMethod invokedMethod, ITestResult testResult) {
        if (!invokedMethod.isTestMethod()) return;

        TestContext ctx = TestContextHolder.get();
        if (ctx == null) return;

        List<SetupAction> reversed = new ArrayList<>(SetupActionRegistry.getActions());
        Collections.reverse(reversed);

        try {
            for (SetupAction action : reversed) {
                try {
                    action.teardown(ctx);
                } catch (Exception e) {
                    log.error("Teardown failed for {}: {}", action.getClass().getSimpleName(), e.getMessage(), e);
                }
            }
        } finally {
            TestContextHolder.clear();
        }
    }
}
