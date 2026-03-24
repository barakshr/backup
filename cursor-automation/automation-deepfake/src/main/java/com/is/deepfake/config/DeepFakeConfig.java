package com.is.deepfake.config;

import com.is.deepfake.testng.DeepFakeChromeOptions;
import com.is.deepfake.testng.DeepFakeEdgeOptions;
import com.is.deepfake.testng.DeepFakeFirefoxOptions;
import com.is.infra.config.AppConfig;
import com.is.infra.config.ConfigManager;
import com.is.infra.selenium.BrowserType;
import com.is.infra.selenium.Options;

import java.util.List;

/**
 * Typed configuration facade for the deepfake module.
 * <p>
 * Implements {@link AppConfig} so it can be registered with
 * {@code AppConfigHolder} as the cross-module config provider.
 * <p>
 * Owns the full mandatory key list for this module and validates it on first
 * access (eager singleton). If any key is missing the entire suite fails fast
 * before a browser opens or any action runs.
 */
public class DeepFakeConfig implements AppConfig {

    private static final DeepFakeConfig INSTANCE = new DeepFakeConfig();

    private final ConfigManager config;

    private DeepFakeConfig() {
        this.config = ConfigManager.get();
    }

    public static DeepFakeConfig get() {
        return INSTANCE;
    }

    // --- AppConfig contract (cross-module) ---

    @Override
    public String getBaseUrl() {
        return config.getRequired("aut.base.url");
    }

    @Override
    public String getTenantId() {
        return config.getRequired("tenant.id");
    }

    @Override
    public BrowserType getBrowserType() {
        return BrowserType.valueOf(config.getRequired("browser.type").toUpperCase());
    }

    @Override
    public Options<?> getBrowserOptions() {
        BrowserType browserType = getBrowserType();
        return switch (browserType) {
            case CHROME -> new DeepFakeChromeOptions();
            case FIREFOX -> new DeepFakeFirefoxOptions();
            case EDGE -> new DeepFakeEdgeOptions();
        };
    }


    // --- deepfake-specific ---

    public boolean isBrowserHeadless() {
        return config.getBoolean("browser.headless", true);
    }

    public String getDemoToolUsername() {
        return config.getRequired("demo.tool.username");
    }

    public String getDemoToolPassword() {
        return config.getRequired("demo.tool.password");
    }

}
