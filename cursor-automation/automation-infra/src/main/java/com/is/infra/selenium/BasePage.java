package com.is.infra.selenium;

import org.openqa.selenium.WebDriver;

/**
 * Base class for all Page Objects.
 * Provides the WebDriver instance and common page interactions.
 * Stub — to be implemented when UI tests are added.
 *
 * All product page classes extend this:
 *   class DeepFakeMeetingsPage extends BasePage { ... }
 */
public abstract class BasePage {

    protected final WebDriver driver;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
    }

    /**
     * Navigates the browser to the given URL.
     */
    public void navigateTo(String url) {
        // TODO: implement
        throw new UnsupportedOperationException("BasePage.navigateTo() not yet implemented");
    }

    /**
     * Returns the current page title.
     */
    public String getTitle() {
        // TODO: implement
        throw new UnsupportedOperationException("BasePage.getTitle() not yet implemented");
    }

    /**
     * Takes a screenshot and returns the bytes.
     * Called by TestListener on test failure.
     */
    public byte[] takeScreenshot() {
        // TODO: implement using TakesScreenshot
        throw new UnsupportedOperationException("BasePage.takeScreenshot() not yet implemented");
    }
}
