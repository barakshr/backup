package com.is.deepfake.tests.ui;

import com.is.common.pages.LoginPage;
import com.is.deepfake.config.DemoToolProperties;
import com.is.deepfake.testng.DeepfakeBaseTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.testng.annotations.Test;

/**
 * UI test: login via DemoTool login page.
 * Driver starts when LoginPage is first used.
 */
public class LoginPageTest extends DeepfakeBaseTest {

    @Autowired
    private DemoToolProperties demoToolProperties;

    @Test(description = "Login to DemoTool")
    public void login() {
        String loginUrl = demoToolProperties.getBaseUrl() + "/login";

        new LoginPage()
                .open(loginUrl)
                .setUserName(demoToolProperties.getUsername())
                .setPassword(demoToolProperties.getPassword())
                .signIn();
    }
}
