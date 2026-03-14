package com.is.deepfake.tests.ui;

import com.is.common.pages.LoginPage;
import com.is.deepfake.testng.DeepfakeBaseTest;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * UI test: navigate to login page and get title.
 * Driver starts when LoginPage is first used (login or getTitle).
 * Driver is closed after the test by SetupOrchestrator.
 */
public class LoginPageTest extends DeepfakeBaseTest {

    @Test(description = "Open login page and verify title is non-empty")
    public void loginAndGetTitle() {
        LoginPage loginPage = new LoginPage();

        loginPage.login("https://example.com/login", "testuser", "testpass");

        String title = loginPage.getTitle();
        assertThat(title)
                .as("Page title after login navigation should not be empty")
                .isNotEmpty();
    }
}
