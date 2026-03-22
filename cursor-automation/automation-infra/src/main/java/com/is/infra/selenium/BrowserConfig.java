package com.is.infra.selenium;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Overrides the browser selection for a specific test method or class.
 * If absent, the browser is resolved from config.properties (browser.type / browser.headless).
 * Driver is created lazily when first needed (e.g. first Page Object use).
 *
 * Example — force Firefox for a specific test:
 *   @BrowserConfig(type = BrowserType.FIREFOX, headless = false)
 *   @Test
 *   public void shouldRenderCorrectlyInFirefox() { ... }
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface BrowserConfig {

    /** Browser to use. DEFAULT resolves from config.properties. */
    BrowserType type() default BrowserType.CHROME;

    /** Whether to run headless. Defaults to true (CI-safe). */
    boolean headless() default true;
}
