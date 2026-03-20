package com.is.deepfake.tests.ui;

import com.is.common.pages.LoginPage;
import com.is.common.pages.assert_page.AssertLoginPage;
import com.is.deepfake.testng.DeepfakeBaseTest;
import com.is.infra.config.ConfigManager;
import org.testng.annotations.Test;

/**
 * UI test: login via DemoTool login page.
 * Driver starts when LoginPage is first used.
 */
public class LoginPageTest extends DeepfakeBaseTest {

    @Test(description = "Login to DemoTool")
    public void login() {
        ConfigManager cfg = ConfigManager.get();
        new LoginPage()
                .setUserName(cfg.getRequired("demo.tool.username"))
                .setPassword(cfg.getRequired("demo.tool.password"))
                .signIn()
                .assertPage(AssertLoginPage.class)
                .checkLogo()
                .returnToPage();
    }
}
