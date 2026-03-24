package com.is.deepfake.testng;

import org.testng.annotations.BeforeSuite;

import com.is.common.testng.CommonBaseTest;
import com.is.deepfake.config.DeepFakeConfig;
import com.is.deepfake.testng.action.CreateDfsTenantAction;
import com.is.deepfake.testng.action.JoinTeamsMeetingAction;
import com.is.infra.config.AppConfigRegister;
import com.is.infra.testng.ActionRegistry;

/**
 * Base class for all Deepfake tests.
 * <p>
 * Registration order in {@link #registerDeepfakeActions()} is intentional:
 * <ol>
 * <li>Config — validates all mandatory keys; fails the suite fast if anything
 * is missing.</li>
 * <li>Cross-module abstractions — AppConfigHolder and DriverHolder depend on
 * config being ready.</li>
 * <li>Actions — depend on driver and config being registered.</li>
 * </ol>
 */
public abstract class DeepfakeBaseTest extends CommonBaseTest {

    @BeforeSuite(alwaysRun = true)
    public void registerDeepfakeActions() {
        AppConfigRegister.register(DeepFakeConfig.get());
        ActionRegistry.register(new CreateDfsTenantAction());
        ActionRegistry.register(new JoinTeamsMeetingAction());
    }
}
