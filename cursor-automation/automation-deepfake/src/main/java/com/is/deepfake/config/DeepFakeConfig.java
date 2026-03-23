package com.is.deepfake.config;

import com.is.infra.config.AppConfig;
import com.is.infra.config.ConfigManager;
import com.is.infra.selenium.BrowserType;

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

    private static final List<String> MANDATORY_KEYS = List.of(
            "aut.base.url",
            "browser.type",
            "browser.headless",
            "demo.tool.username",
            "demo.tool.password"
    );

    private static final DeepFakeConfig INSTANCE = new DeepFakeConfig();

    private final ConfigManager config;

    private DeepFakeConfig() {
        this.config = ConfigManager.get();
        config.validateKeys(MANDATORY_KEYS);
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

    // --- deepfake-specific ---

    public BrowserType getBrowserType() {
        return BrowserType.valueOf(config.getRequired("browser.type").toUpperCase());
    }

    public boolean isBrowserHeadless() {
        return config.getBoolean("browser.headless", false);
    }

    public String getDemoToolUsername() {
        return config.getRequired("demo.tool.username");
    }

    public String getDemoToolPassword() {
        return config.getRequired("demo.tool.password");
    }
}
