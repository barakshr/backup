package com.is.infra.config;

import com.is.infra.selenium.BrowserType;
import com.is.infra.selenium.Options;

/**
 * Cross-module application configuration contract.
 * <p>
 * Product modules implement this interface and register an instance via
 * {@link AppConfigRegister#register(AppConfig)} in their
 * {@code @BeforeSuite}.
 * Common and infra classes read values through {@link AppConfigRegister} —
 * they have no knowledge of which product module is providing the values.
 */
public interface AppConfig {

    String getBaseUrl();

    String getTenantId();

    BrowserType getBrowserType();

    Options<?> getBrowserOptions();
}
