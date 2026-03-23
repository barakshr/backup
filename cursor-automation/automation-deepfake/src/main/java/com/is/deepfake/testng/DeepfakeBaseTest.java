package com.is.deepfake.testng;

import com.is.common.testng.CommonBaseTest;
import com.is.deepfake.config.DeepFakeConfig;
import com.is.deepfake.testng.action.CreateDfsTenantAction;
import com.is.deepfake.testng.action.JoinTeamsMeetingAction;
import com.is.infra.config.AppConfigHolder;
import com.is.infra.selenium.DriverHolder;
import com.is.infra.testng.ActionRegistry;
import org.testng.annotations.BeforeSuite;

/**
 * Base class for all Deepfake tests.
 * <p>
 * Registration order in {@link #registerDeepfakeActions()} is intentional:
 * <ol>
 *   <li>Config — validates all mandatory keys; fails the suite fast if anything is missing.</li>
 *   <li>Cross-module abstractions — AppConfigHolder and DriverHolder depend on config being ready.</li>
 *   <li>Actions — depend on driver and config being registered.</li>
 * </ol>
 */
public abstract class DeepfakeBaseTest extends CommonBaseTest {

    @BeforeSuite(alwaysRun = true)
    public void registerDeepfakeActions() {
        // 1. Config first — validates mandatory keys, fails fast if anything is missing
        DeepFakeConfig cfg = DeepFakeConfig.get();

        // 2. Register cross-module abstractions
        AppConfigHolder.register(cfg);
        DriverHolder.register(DriverRegisterDeepFake.get());

        // 3. Register actions
        ActionRegistry.register(new CreateDfsTenantAction());
        ActionRegistry.register(new JoinTeamsMeetingAction());
    }
}
