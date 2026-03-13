package com.is.infra.selenium;

import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Base class for all Page Objects.
 * Driver is obtained lazily from DriverHolder — first use of a Page Object starts the browser.
 *
 * All product page classes extend this:
 *   class LoginPage extends BasePage { ... }
 */
public abstract class BasePage {

    /**
     * Returns the WebDriver for the current thread, creating it on first call (reads config).
     */
    protected WebDriver getDriver() {
        return DriverHolder.getDriver();
    }

    /**
     * Navigates the browser to the given URL.
     */
    public void navigateTo(String url) {
        getDriver().get(url);
    }

    /**
     * Returns the current page title.
     */
    public String getTitle() {
        return getDriver().getTitle();
    }

    /**
     * Takes a screenshot. Used by TestListener on test failure.
     */
    public byte[] takeScreenshot() {
        WebDriver driver = getDriver();
        if (driver instanceof TakesScreenshot ts) {
            return ts.getScreenshotAs(OutputType.BYTES);
        }
        return new byte[0];
    }
}
