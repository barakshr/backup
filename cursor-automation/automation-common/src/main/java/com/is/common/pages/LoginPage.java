package com.is.common.pages;

import com.is.infra.selenium.BasePage;

/**
 * Page Object for the login screen (IronScales / shared login).
 * Driver starts when this page is first used (e.g. getTitle() or login()).
 */
public class LoginPage extends BasePage {

    @Override
    public String getTitle() {
        return super.getTitle();
    }

    /**
     * Navigates to the login page. Credentials are not yet filled (TODO: add selectors and submit).
     * First call starts the driver.
     */
    public void login(String loginPageUrl, String username, String password) {
        navigateTo(loginPageUrl);
        // TODO: find username/password fields, fill and submit when selectors are defined
    }
}
