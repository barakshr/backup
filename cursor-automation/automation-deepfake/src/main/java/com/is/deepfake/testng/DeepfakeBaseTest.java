package com.is.deepfake.testng;

import com.is.common.testng.CommonBaseTest;
import com.is.deepfake.testng.action.CreateDfsTenantAction;
import com.is.deepfake.testng.action.JoinTeamsMeetingAction;
import com.is.infra.testng.ActionRegistry;
import org.testng.annotations.BeforeSuite;

/**
 * Base class for all Deepfake tests.
 * <p>
 * Deepfake-specific actions are registered in {@link #registerDeepfakeActions()}.
 */
public abstract class DeepfakeBaseTest extends CommonBaseTest {

    @BeforeSuite(alwaysRun = true)
    public void registerDeepfakeActions() {
        ActionRegistry.register(new CreateDfsTenantAction());
        ActionRegistry.register(new JoinTeamsMeetingAction());
    }
}
