package com.is.infra.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Central configuration loader. Loads from a properties file on the classpath.
 * Environment variables override properties file values — this allows Jenkins
 * to inject secrets and environment-specific values without modifying files.
 *
 * Priority (highest to lowest):
 *   1. Environment variable (e.g. DEMO_TOOL_BASE_URL)
 *   2. Properties file value (e.g. demo.tool.base.url)
 *   3. Default value provided at call site
 */
public class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);
    private static final String DEFAULT_CONFIG_FILE = "config.properties";

    private final Properties properties;

    private ConfigManager(Properties properties) {
        this.properties = properties;
    }

    /**
     * Loads the default config.properties from the classpath.
     */
    public static ConfigManager load() {
        return load(DEFAULT_CONFIG_FILE);
    }

    /**
     * Loads a named properties file from the classpath.
     *
     * @param fileName the properties file name (relative to classpath root)
     */
    public static ConfigManager load(String fileName) {
        Properties props = new Properties();
        try (InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                log.warn("Config file '{}' not found on classpath. Using env vars only.", fileName);
            } else {
                props.load(in);
                log.info("Loaded config from '{}'", fileName);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + fileName, e);
        }
        return new ConfigManager(props);
    }

    /**
     * Returns the value for the given key.
     * Env var key is derived by uppercasing and replacing dots with underscores.
     * e.g. "demo.tool.base.url" → checks env var "DEMO_TOOL_BASE_URL" first.
     *
     * @param key property key
     * @return value or null if not found
     */
    public String getString(String key) {
        String envKey = toEnvVarKey(key);
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isBlank()) {
            log.debug("Config [{}] resolved from env var [{}]", key, envKey);
            return envValue;
        }
        return properties.getProperty(key);
    }

    /**
     * Returns the value for the given key, or a default if not found.
     */
    public String getString(String key, String defaultValue) {
        String value = getString(key);
        return (value != null && !value.isBlank()) ? value : defaultValue;
    }

    /**
     * Returns the value as an integer.
     */
    public int getInt(String key, int defaultValue) {
        String value = getString(key);
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            log.warn("Config key '{}' has non-integer value '{}', using default {}", key, value, defaultValue);
            return defaultValue;
        }
    }

    /**
     * Returns the value as a boolean.
     */
    public boolean getBoolean(String key, boolean defaultValue) {
        String value = getString(key);
        if (value == null || value.isBlank()) return defaultValue;
        return Boolean.parseBoolean(value.trim());
    }

    /**
     * Throws if the key is missing. Use for required config values.
     */
    public String getRequired(String key) {
        String value = getString(key);
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Required config key '" + key + "' is not set. " +
                    "Set it in config.properties or via env var '" + toEnvVarKey(key) + "'"
            );
        }
        return value;
    }

    private static String toEnvVarKey(String key) {
        return key.toUpperCase().replace(".", "_").replace("-", "_");
    }
}
