package com.is.infra.selenium;

/**
 * Supported browser types for WebDriver creation.
 *
 * DEFAULT is resolved at runtime from config.properties (browser.type).
 * Used by @BrowserConfig to express a per-test override.
 */
public enum BrowserType {
    CHROME,
    FIREFOX,
    EDGE,
    DEFAULT
}
