package com.is.infra.testng;

import com.is.infra.selenium.DriverFactory;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

/**
 * Base class for all UI tests.
 * Extends BaseTest and manages WebDriver lifecycle per test method.
 * Stub — to be implemented when Selenium tests are added.
 *
 * Product UI test base classes extend this:
 *   BaseDeepfakeUiTest extends BaseUiTest
 */
public class BaseUiTest extends BaseTest {

    protected WebDriver driver;

    @BeforeMethod(alwaysRun = true)
    public void setupDriver() {
        // TODO: read browser type from config and create driver via DriverFactory
        // driver = DriverFactory.create();
    }

    @AfterMethod(alwaysRun = true)
    public void teardownDriver() {
        DriverFactory.quit(driver);
    }
}
