package com.is.infra.selenium;

import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Thread-local holder for the current test's WebDriver.
 *
 * Driver is created lazily on first call to getDriver() — e.g. when a Page
 * Object is first used.
 *
 * DRIVER_REGISTER is a suite-wide static field (not ThreadLocal) so that a
 * single call to register() from @BeforeSuite is visible to all test threads
 * in parallel execution. The WebDriver itself remains ThreadLocal so each
 * thread gets its own browser instance.
 *
 * Call quitAndClear() after each test method (e.g. from ActionOrchestrator).
 * Thread-safe: each thread has its own driver instance.
 */
public class DriverHolder {

    private static final Logger log = LoggerFactory.getLogger(DriverHolder.class);

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    private static volatile DriverRegister DRIVER_REGISTER;

    public static void register(DriverRegister driverRegister) {
        DRIVER_REGISTER = driverRegister;
    }

    public static WebDriver getDriver() {
        WebDriver driver = DRIVER.get();

        if (driver == null) {
            setDriver();
        }
        return DRIVER.get();
    }

    private static WebDriver setDriver() {
        DriverRegister driverRegister = DRIVER_REGISTER;
        if (driverRegister == null) {
            driverRegister = new DefaultChromeRegister();
        }
        WebDriver driver = DriverFactory.newCreate(driverRegister);
        DRIVER.set(driver);
        return driver;
    }

    public static WebDriver setDriver(WebDriver driver) {
        if (DRIVER.get() != null) {
            throw new IllegalStateException("Driver already set for this thread");
        }
        DRIVER.set(driver);
        return driver;
    }

    /**
     * Returns true if the current thread has a driver (created by a previous
     * getDriver() call).
     */
    public static boolean hasDriver() {
        return DRIVER.get() != null;
    }

    /**
     * Quits the driver for the current thread (if any) and clears the holder.
     * Call this after every test method so the next test gets a fresh driver or
     * none.
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
