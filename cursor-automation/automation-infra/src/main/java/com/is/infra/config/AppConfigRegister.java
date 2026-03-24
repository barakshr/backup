package com.is.infra.config;

import com.is.infra.selenium.BrowserType;
import com.is.infra.selenium.Options;

/**
 * Suite-wide holder for the registered {@link AppConfig}.
 * <p>
 * The product module registers its implementation once in {@code @BeforeSuite}.
 * Common and infra classes read application config through this holder without
 * any knowledge of the concrete product module providing the values.
 * <p>
 * Uses a plain {@code volatile} field (not ThreadLocal) so the registration
 * performed on the suite thread is visible to all parallel test threads.
 */
public class AppConfigRegister {

    private static volatile AppConfig APP_CONFIG;

    public static void register(AppConfig appConfig) {
        APP_CONFIG = appConfig;
    }

    public static String getBaseUrl() {
        return require().getBaseUrl();
    }

    public static String getTenantId() {
        return require().getTenantId();
    }

    public static BrowserType getBrowserType() {
        return require().getBrowserType();
    }

    public static Options<?> getBrowserOptions() {
        return require().getBrowserOptions();
    }

    private static AppConfig require() {
        if (APP_CONFIG == null) {
            throw new IllegalStateException(
                    "No AppConfig registered. Call AppConfigHolder.register() in @BeforeSuite.");
        }
        return APP_CONFIG;
    }
}
