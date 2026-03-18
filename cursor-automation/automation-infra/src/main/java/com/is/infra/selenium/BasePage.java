package com.is.infra.selenium;

import org.openqa.selenium.By;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Base class for all Page Objects.
 * Driver is obtained lazily from DriverHolder — first use of a Page Object starts the browser.
 *
 * All element interactions wait before acting (explicit wait via WebDriverWait).
 */
public abstract class BasePage { 

    protected static final Duration DEFAULT_WAIT = Duration.ofSeconds(10);

    private final WebDriver driver=DriverHolder.getDriver();

    public BasePage() {
        checkDriver();  // check if driver is found before navigating to the page
    }



    public BasePage(String url) {
        this();
        navigateTo(url);
    }

    /**
     * Returns the WebDriver for the current thread, creating it on first call (reads config).
     */
    protected WebDriver getDriver() {
        return driver;
    }

    /**
     * Waits for element to be visible, then returns it.
     */
    protected WebElement find(By locator) {
        return new WebDriverWait(getDriver(), DEFAULT_WAIT)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * Waits for element to be clickable, then returns it.
     */
    protected WebElement findClickable(By locator) {
        return new WebDriverWait(getDriver(), DEFAULT_WAIT)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Waits for element, then clicks it.
     */
    protected BasePage click(By locator) {
        findClickable(locator).click();
        return this;
    }

    /**
     * Waits for element, then types into it (clears first).
     */
    protected BasePage type(By locator, String text) {
        WebElement el = find(locator);
        el.clear();
        el.sendKeys(text);
        return this;
    }


    /**
     * Waits for element, then returns its text.
     */
    protected String getText(By locator) {
        return find(locator).getText();
    }

    /**
     * Navigates the browser to the given URL.
     */
    public BasePage navigateTo(String url) {
        getDriver().get(url);
        return this;
    }

    /**
     * Navigates to the given URL and returns this page for chaining.
     */
    @SuppressWarnings("unchecked")
    public <T extends BasePage> T open(String url) {
        navigateTo(url);
        return (T) this;
    }

    /**
     * Creates a page instance via reflection (no-arg constructor).
     */
    protected <T extends BasePage> T page(Class<T> pageClass) {
        try {
            return pageClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Cannot create page: " + pageClass.getSimpleName(), e);
        }
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

    private void checkDriver() {
        if (driver == null) {
          throw new IllegalStateException("Driver not found");
        }
      }
}
