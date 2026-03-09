package com.is.infra.selenium;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Builds browser configuration options.
 * Stub — to be implemented when UI tests are added.
 */
public class DriverOptions {

    private DriverOptions() {}

    /**
     * Returns ChromeOptions configured for CI (headless, no sandbox, disable GPU).
     * TODO: implement
     */
    public static ChromeOptions headlessChrome() {
        ChromeOptions options = new ChromeOptions();
        // TODO: add --headless, --no-sandbox, --disable-dev-shm-usage, etc.
        return options;
    }

    /**
     * Returns ChromeOptions for local development (headed).
     * TODO: implement
     */
    public static ChromeOptions headedChrome() {
        return new ChromeOptions();
    }

    /**
     * Returns FirefoxOptions configured for CI.
     * TODO: implement
     */
    public static FirefoxOptions headlessFirefox() {
        FirefoxOptions options = new FirefoxOptions();
        // TODO: add headless mode
        return options;
    }
}
