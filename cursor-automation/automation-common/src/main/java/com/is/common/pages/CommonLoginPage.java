package com.is.common.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import com.is.infra.selenium.BasePage;

/**
 * Page Object for the login screen.
 * Driver starts when this page is first used.
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

    @SuppressWarnings("unchecked")
    @Override
    public CommonLoginPage open(String url) {
        super.open(url);
        return this;
    }

}
