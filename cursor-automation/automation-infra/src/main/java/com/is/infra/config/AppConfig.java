package com.is.infra.config;

/**
 * Cross-module application configuration contract.
 * <p>
 * Product modules implement this interface and register an instance via
 * {@link AppConfigHolder#register(AppConfig)} in their {@code @BeforeSuite}.
 * Common and infra classes read values through {@link AppConfigHolder} —
 * they have no knowledge of which product module is providing the values.
 */
public interface AppConfig {

    String getBaseUrl();

    String getTenantId();
}
