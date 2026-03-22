package com.is.common.pages;

import com.is.infra.config.ConfigManager;
import com.is.infra.selenium.BasePage;
import com.is.infra.selenium.BrowserType;
import com.is.infra.selenium.DriverHolder;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;


/**
 * Page Object for the login screen.
 * Driver starts when this page is first used.
 */
public class LoginPage extends BasePage {
    private static final By USERNAME = By.name("username");
    private static final By PASSWORD = By.name("password");


    public LoginPage() {
        super();

    }


    public LoginPage(WebDriver driver) {
        super(driver);
    }

    public LoginPage setUserName(String username) {
        type(USERNAME, username);
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
