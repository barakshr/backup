package com.is.common.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.is.infra.config.AppConfigRegister;
import com.is.infra.selenium.BasePage;

/**
 * Page Object for the login screen.
 * Driver is created lazily when this page is first used.
 * <p>
 * Call {@link #open()} to navigate to the AUT base URL supplied by the
 * product module via {@link AppConfigRegister}. The page has no knowledge of
 * which product module provides the URL.
 */
public class CommonLoginPage extends BasePage {
    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");

    public CommonLoginPage() {
        super();
    }

    public CommonLoginPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Navigates to the AUT base URL registered by the product module.
     */
    public CommonLoginPage open() {
        super.open(AppConfigRegister.getBaseUrl());
        return this;
    }

    public CommonLoginPage setUserName(String username) {
        type(USERNAME, username);
        return this;
    }

    public CommonLoginPage setPassword(String password) {
        type(PASSWORD, password);
        return this;
    }

    public CommonLoginPage signIn() {
        return this;
    }

    public <T extends BasePage> T signIn(Class<T> nextPage) {
        return goToPage(nextPage);
    }
}
