package com.is.deepfake.config;

import com.is.deepfake.testng.DeepFakeChromeOptions;
import com.is.deepfake.testng.DeepFakeEdgeOptions;
import com.is.deepfake.testng.DeepFakeFirefoxOptions;
import com.is.infra.config.AppConfig;
import com.is.infra.config.ConfigManager;
import com.is.infra.selenium.BrowserType;
import com.is.infra.selenium.Options;




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


    public String getDemoToolBaseUrl() {
        return config.getRequired("demo.tool.base.url");
    }



    public boolean isBrowserHeadless() {
        return config.getBoolean("browser.headless", true);
    }

    public String getDemoToolUsername() {
        return config.getRequired("demo.tool.username");
    }

    public String getDemoToolPassword() {
        return config.getRequired("demo.tool.password");
    }

    public String getNotificationBotBaseUrl() {
        return config.getRequired("notification.bot.base.url");
    }

    // --- Call Server ---

    public String getCallServerBaseUrl() {
        return config.getRequired("call.server.base.url");
    }

    // --- Database ---

    public String getDbHost() {
        return config.getRequired("db.host");
    }

    public int getDbPort() {
        return config.getInt("db.port", 5432);
    }

    public String getDbName() {
        return config.getRequired("db.name");
    }

    public String getDbUser() {
        return config.getRequired("db.user");
    }

    public String getDbPassword() {
        return config.getRequired("db.password");
    }

}
