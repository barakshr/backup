package com.is.infra.selenium;

import org.openqa.selenium.WebDriver;

/**
 * Creates and configures WebDriver instances.
 * Stub — to be implemented when UI tests are added.
 *
 * Intended to support: Chrome (headless/headed), Firefox, remote Grid.
 * Config keys: browser.type, browser.headless, browser.remote.url
 */
public class DriverFactory {

    private DriverFactory() {}

    /**
     * Creates a WebDriver instance based on the provided config.
     * TODO: read browser type and headless flag from ConfigManager.
     */
    public static WebDriver create() {
        throw new UnsupportedOperationException("DriverFactory.create() not yet implemented");
    }

    /**
     * Creates a headless Chrome WebDriver.
     * TODO: implement using WebDriverManager or Selenium Manager.
     */
    public static WebDriver createHeadlessChrome() {
        throw new UnsupportedOperationException("DriverFactory.createHeadlessChrome() not yet implemented");
    }

    /**
     * Quits the driver and releases resources.
     */
    public static void quit(WebDriver driver) {
        if (driver != null) {
            driver.quit();
        }
    }
}
