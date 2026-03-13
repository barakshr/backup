package com.is.infra.testng.setup;

import com.is.infra.config.ConfigManager;
import com.is.infra.selenium.BrowserConfig;
import com.is.infra.selenium.BrowserType;
import com.is.infra.testng.TestContext;
import com.is.infra.testng.annotation.TestSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;

/**
 * Registers browser intent in TestContext when @TestSetup(requiresBrowser=true).
 *
 * Does NOT open the browser — only records the browser type and headless flag.
 * The actual WebDriver is created lazily on the first call to TestContext.getDriver(),
 * which happens when a Page Object is first constructed in the test body.
 *
 * Browser resolution order (highest to lowest priority):
 *   1. @BrowserConfig(type=..., headless=...) on the test method or class
 *   2. config.properties: browser.type / browser.headless
 *   3. Fallback: CHROME, headless=true
 *
 * Teardown: calls ctx.quitDriver() — no-op if the browser was never actually opened.
 */
public class StartBrowserAction extends AbstractSetupAction {

    private static final Logger log = LoggerFactory.getLogger(StartBrowserAction.class);

    private final ConfigManager config;

    public StartBrowserAction(ConfigManager config) {
        this.config = config;
    }

    @Override
    public boolean appliesTo(Method method) {
        TestSetup setup = getAnnotation(method, TestSetup.class);
        return setup != null && setup.requiresBrowser();
    }

    @Override
    public void setup(TestContext.Builder builder, Method method) {
        BrowserType type     = resolveBrowserType(method);
        boolean     headless = resolveHeadless(method);
        log.info("Browser intent registered: {} (headless={})", type, headless);
        builder.withBrowser(type, headless);
    }

    @Override
    public void teardown(TestContext ctx) {
        ctx.quitDriver();
    }

    private BrowserType resolveBrowserType(Method method) {
        BrowserConfig override = getAnnotation(method, BrowserConfig.class);
        if (override != null && override.type() != BrowserType.DEFAULT) {
            return override.type();
        }
        String name = config.getString("browser.type", "chrome").toUpperCase();
        try {
            return BrowserType.valueOf(name);
        } catch (IllegalArgumentException e) {
            log.warn("Unknown browser.type '{}' in config, defaulting to CHROME", name);
            return BrowserType.CHROME;
        }
    }

    private boolean resolveHeadless(Method method) {
        BrowserConfig override = getAnnotation(method, BrowserConfig.class);
        if (override != null) {
            return override.headless();
        }
        return config.getBoolean("browser.headless", true);
    }
}
