package com.is.infra.selenium;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates and manages WebDriver instances.
 *
 * Browser binaries are managed by Selenium Manager (bundled with Selenium
 * 4.6+).
 * No external WebDriverManager dependency is required.
 *
 * Browser resolution:
 * - create(BrowserType, headless) — explicit type; DEFAULT resolved from
 * config.
 * - create() — reads browser.type and browser.headless from config.
 *
 * Quit:
 * - quit(driver) — null-safe; swallows exceptions to keep teardown reliable.
 */
public class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    public static WebDriver create(DriverRegister driverRegister) {
        BrowserType browserType = driverRegister.getBrowserType();
        Options<?> options = driverRegister.getOptions();
        log.info("Creating {} driver", browserType);
        return switch (browserType) {
            case CHROME -> new ChromeDriver((ChromeOptions) options.getOptions());
            case FIREFOX -> new FirefoxDriver((org.openqa.selenium.firefox.FirefoxOptions) options.getOptions());
            case EDGE -> new EdgeDriver((org.openqa.selenium.edge.EdgeOptions) options.getOptions());
        };
    }

    /** Null-safe driver quit. Logs but does not rethrow on error. */
    public static void quit(WebDriver driver) {
        if (driver != null) {
            try {
                driver.quit();
                log.debug("WebDriver quit successfully");
            } catch (Exception e) {
                log.warn("Failed to quit WebDriver: {}", e.getMessage());
            }
        }
    }

}
