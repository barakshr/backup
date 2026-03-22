package com.is.deepfake.tests.ui;

import org.testng.annotations.Test;

import com.is.common.pages.CommonLoginPage;
import com.is.deepfake.selenium.pages.NextPage;
import com.is.deepfake.testng.DeepfakeBaseTest;
import com.is.infra.config.ConfigManager;

/**
 * UI test: login via DemoTool login page.
 * Driver starts when LoginPage is first used.
 */
public class LoginPageTest extends DeepfakeBaseTest {

    @Test(description = "Login to DemoTool")
    public void login() {
        ConfigManager cfg = ConfigManager.get();
        new CommonLoginPage()
                .setUserName(cfg.getRequired("demo.tool.username"))
                .setPassword(cfg.getRequired("demo.tool.password"))
                .signIn(NextPage.class).clickonMe().returnToLoginPage();
    }
}
