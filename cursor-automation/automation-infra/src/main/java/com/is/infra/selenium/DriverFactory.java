package com.is.infra.selenium;

import com.is.infra.config.ConfigManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Creates and manages WebDriver instances.
 *
 * Browser binaries are managed by Selenium Manager (bundled with Selenium 4.6+).
 * No external WebDriverManager dependency is required.
 *
 * Browser resolution:
 *   - create(BrowserType, headless) — explicit type; DEFAULT resolved from config.
 *   - create()                      — reads browser.type and browser.headless from config.
 *
 * Quit:
 *   - quit(driver) — null-safe; swallows exceptions to keep teardown reliable.
 */
public class DriverFactory {

    private static final Logger log = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {}

    public static WebDriver create(BrowserType browserType, boolean headless) {
        BrowserType resolved = resolve(browserType);
        log.info("Creating {} driver (headless={})", resolved, headless);
        return switch (resolved) {
            case CHROME  -> headless ? new ChromeDriver(DriverOptions.headlessChrome())
                                     : new ChromeDriver(DriverOptions.headedChrome());
            case FIREFOX -> headless ? new FirefoxDriver(DriverOptions.headlessFirefox())
                                     : new FirefoxDriver(DriverOptions.headedFirefox());
            case EDGE    -> headless ? new EdgeDriver(DriverOptions.headlessEdge())
                                     : new EdgeDriver(DriverOptions.headedEdge());
            default      -> throw new IllegalStateException("Unresolved browser type: " + resolved);
        };
    }

    /** Creates a driver using browser.type and browser.headless from config.properties. */
    public static WebDriver create() {
        ConfigManager config   = ConfigManager.load();
        String        name     = config.getString("browser.type", "chrome").toUpperCase();
        boolean       headless = config.getBoolean("browser.headless", true);
        BrowserType   type;
        try {
            type = BrowserType.valueOf(name);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown browser.type '{}' in config, defaulting to CHROME", name);
            type = BrowserType.CHROME;
        }
        return create(type, headless);
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

    private static BrowserType resolve(BrowserType type) {
        if (type == null || type == BrowserType.DEFAULT) {
            return BrowserType.valueOf(
                    ConfigManager.load().getString("browser.type", "chrome").toUpperCase());
        }
        return type;
    }
}
