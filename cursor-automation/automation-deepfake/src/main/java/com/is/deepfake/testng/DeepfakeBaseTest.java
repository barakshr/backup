package com.is.deepfake.testng;

import org.testng.annotations.BeforeSuite;

import com.is.common.testng.CommonBaseTest;
import com.is.deepfake.testng.action.CreateDfsTenantAction;
import com.is.deepfake.testng.action.JoinTeamsMeetingAction;
import com.is.infra.selenium.DriverHolder;
import com.is.infra.testng.ActionRegistry;

/**
 * Base class for all Deepfake tests.
 * <p>
 * Deepfake-specific actions are registered in
 * {@link #registerDeepfakeActions()}.
 */
public abstract class DeepfakeBaseTest extends CommonBaseTest {

    @BeforeSuite(alwaysRun = true)
    public void registerDeepfakeActions() {
        DriverHolder.register(DriverRegisterDeepFake.getInstance());
        ActionRegistry.register(new CreateDfsTenantAction());
        ActionRegistry.register(new JoinTeamsMeetingAction());
    }
}
