package com.is.deepfake.tests.ui;

import org.testng.annotations.Test;

import com.is.common.pages.CommonLoginPage;
import com.is.common.testng.annotation.CommonAnnotation;
import com.is.deepfake.config.DeepFakeConfig;
import com.is.deepfake.selenium.pages.NextPage;
import com.is.deepfake.testng.DeepfakeBaseTest;
import com.is.deepfake.testng.annotation.DeepfakeAnnotation;

/**
 * UI test: login via DemoTool login page.
 * Driver starts lazily when CommonLoginPage is first used.
 */
public class LoginPageTest extends DeepfakeBaseTest {

    @Test(description = "Login to DemoTool")
    @DeepfakeAnnotation(createDfsTenant = true, joinTeamsMeeting = true)
    @CommonAnnotation(createCompany = true)
    
    public void login() {
        DeepFakeConfig cfg = DeepFakeConfig.get();
        new CommonLoginPage()
                .open()
                .setUserName(cfg.getDemoToolUsername())
                .setPassword(cfg.getDemoToolPassword())
                .signIn(NextPage.class);
    }
}
