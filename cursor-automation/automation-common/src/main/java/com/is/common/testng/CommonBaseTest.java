package com.is.common.testng;

import org.testng.annotations.BeforeSuite;

import com.is.common.testng.action.CreateCompanyAction;
import com.is.infra.testng.BaseTest;
import com.is.infra.testng.SetupActionRegistry;

public class CommonBaseTest extends BaseTest {
    @BeforeSuite(alwaysRun = true)
    public void registerCommonActions() {
        SetupActionRegistry.register(new CreateCompanyAction());
    }
}
