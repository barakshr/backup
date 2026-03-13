package com.is.infra.selenium;

import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;

/**
 * Builds browser configuration options for each supported browser.
 *
 * Headless options are CI-safe: no sandbox, shared memory workarounds, fixed viewport.
 * Headed options are for local development and debugging.
 */
public class DriverOptions {

    private DriverOptions() {}

    public static ChromeOptions headlessChrome() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");
        options.addArguments("--disable-extensions");
        return options;
    }

    public static ChromeOptions headedChrome() {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--window-size=1920,1080");
        return options;
    }

    public static FirefoxOptions headlessFirefox() {
        FirefoxOptions options = new FirefoxOptions();
        options.addArguments("-headless");
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        return options;
    }

    public static FirefoxOptions headedFirefox() {
        return new FirefoxOptions();
    }

    public static EdgeOptions headlessEdge() {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--headless=new");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--window-size=1920,1080");
        return options;
    }

    public static EdgeOptions headedEdge() {
        return new EdgeOptions();
    }
}
