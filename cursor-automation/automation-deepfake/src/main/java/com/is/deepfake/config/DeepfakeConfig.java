package com.is.deepfake.config;

import com.is.infra.config.ConfigManager;

/**
 * Deepfake-specific configuration keys.
 * Reads from the shared ConfigManager — infra loads the file,
 * this class knows which keys belong to the DF team.
 *
 * Config keys (set in config.properties or env vars):
 *   demo.tool.base.url      → DEMO_TOOL_BASE_URL
 *   demo.tool.username      → DEMO_TOOL_USERNAME
 *   demo.tool.password      → DEMO_TOOL_PASSWORD
 *   call.server.base.url    → CALL_SERVER_BASE_URL
 *   notification.bot.base.url → NOTIFICATION_BOT_BASE_URL
 */
public class DeepfakeConfig {

    private final ConfigManager config;

    public DeepfakeConfig(ConfigManager config) {
        this.config = config;
    }

    public String getDemoToolBaseUrl() {
        return config.getRequired("demo.tool.base.url");
    }

    public String getDemoToolUsername() {
        return config.getRequired("demo.tool.username");
    }

    public String getDemoToolPassword() {
        return config.getRequired("demo.tool.password");
    }

    public String getCallServerBaseUrl() {
        return config.getString("call.server.base.url", "");
    }

    public String getNotificationBotBaseUrl() {
        return config.getString("notification.bot.base.url", "");
    }
}
