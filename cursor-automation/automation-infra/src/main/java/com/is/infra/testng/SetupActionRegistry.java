package com.is.infra.testng;

import com.is.infra.testng.setup.SetupAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Central registry of all SetupAction implementations across all modules.
 *
 * Registration happens in @BeforeSuite methods:
 *   - BaseTest registers infra actions (StartBrowserAction).
 *   - Product base tests register product-specific actions (CreateCompanyAction, etc.).
 *
 * Deduplicates by class: if a @BeforeSuite fires more than once due to TestNG
 * suite configuration, the same action is not registered twice.
 *
 * Execution order = registration order.
 * Teardown order  = reverse of registration order (guaranteed by SetupOrchestrator).
 */
public class SetupActionRegistry {

    private static final Logger log = LoggerFactory.getLogger(SetupActionRegistry.class);

    private static final List<SetupAction> actions      = new CopyOnWriteArrayList<>();
    private static final Set<Class<?>> registered   = Collections.newSetFromMap(new ConcurrentHashMap<>());

    private SetupActionRegistry() {}

    public static void register(SetupAction action) {
        if (registered.add(action.getClass())) {
            actions.add(action);
            log.info("Registered SetupAction: {}", action.getClass().getSimpleName());
        }
    }

    public static List<SetupAction> getActions() {
        return Collections.unmodifiableList(actions);
    }

    /** Package-private: used only in unit tests of the registry itself. */
    static void reset() {
        actions.clear();
        registered.clear();
    }
}
