package com.is.common.pages;

import com.is.infra.config.ConfigManager;
import com.is.infra.selenium.BasePage;
import org.openqa.selenium.By;

/**
 * Page Object for the login screen.
 * Driver starts when this page is first used.
 */
public class LoginPage extends BasePage {

    public LoginPage() {
        super(ConfigManager.get().getRequired("aut.base.url"));
    }

    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");
    private static final By SIGN_IN_BUTTON = By.className("is-button-internal-container");

    public LoginPage setUserName(String username) {
        //type(USERNAME, username);
        return this;
    }

    public LoginPage setPassword(String password) {
        type(PASSWORD, password);
        return this;
    }

    public LoginPage signIn() {
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public LoginPage open(String url) {
        super.open(url);
        return this;
    }

    
}
