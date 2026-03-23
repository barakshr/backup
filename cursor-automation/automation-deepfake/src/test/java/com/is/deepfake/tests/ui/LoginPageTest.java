package com.is.deepfake.tests.ui;

import com.is.common.pages.CommonLoginPage;
import com.is.deepfake.config.DeepFakeConfig;
import com.is.deepfake.selenium.pages.NextPage;
import com.is.deepfake.testng.DeepfakeBaseTest;
import org.testng.annotations.Test;

/**
 * UI test: login via DemoTool login page.
 * Driver starts lazily when CommonLoginPage is first used.
 */
public class LoginPageTest extends DeepfakeBaseTest {

    @Test(description = "Login to DemoTool")
    public void login() {
        DeepFakeConfig cfg = DeepFakeConfig.get();
        new CommonLoginPage()
                .open()
                .setUserName(cfg.getDemoToolUsername())
                .setPassword(cfg.getDemoToolPassword())
                .signIn(NextPage.class);
    }
}
