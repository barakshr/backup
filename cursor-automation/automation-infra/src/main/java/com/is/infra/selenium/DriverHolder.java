package com.is.infra.selenium;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-local holder for the current test's WebDriver.
 *
 * Driver is created lazily on first call to getDriver() — e.g. when a Page Object
 * is first used. Config (browser.type, browser.headless) is read at creation time.
 *
 * Call quitAndClear() after each test method (e.g. from SetupOrchestrator).
 * Thread-safe: each thread has its own driver instance.
 */
public class DriverHolder {

    private static final Logger log = LoggerFactory.getLogger(DriverHolder.class);

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private DriverHolder() {}

    /**
     * Returns the WebDriver for the current thread, creating it on first call
     * using config (browser.type, browser.headless). Subsequent calls return
     * the same instance until quitAndClear() is invoked.
     */
    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();
        if (driver == null) {
            driver = DriverFactory.create();
            DRIVER.set(driver);
            log.info("WebDriver created for thread (lazy)");
        }
        return driver;
    }

    /**
     * Returns true if the current thread has a driver (created by a previous getDriver() call).
     */
    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    /**
     * Quits the driver for the current thread (if any) and clears the holder.
     * Call this after every test method so the next test gets a fresh driver or none.
     */
    public static void quitAndClear() {
        WebDriver driver = DRIVER.get();
        if (driver != null) {
            DriverFactory.quit(driver);
            log.info("WebDriver quit and cleared for thread");
        }
        DRIVER.remove();
    }
}
