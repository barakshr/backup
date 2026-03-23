package com.is.infra.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Global configuration singleton. Loads once per JVM run, then shared by all code.
 * <p>
 * Load order (later wins for the same key, except env vars always win at read time):
 * <ol>
 *   <li>{@code infra-defaults.properties} — infra baseline on the classpath</li>
 *   <li>{@code config.properties} — module-specific (e.g. deepfake {@code src/test/resources})</li>
 * </ol>
 * Environment variables override file values on every {@link #getString(String)} read.
 * <p>
 * Infra defines no mandatory keys. Each module calls {@link #validateKeys(List)} from its
 * own {@code @BeforeSuite} to validate the keys it requires.
 */
public class ConfigManager {

    private static final Logger log = LoggerFactory.getLogger(ConfigManager.class);

    private static final String INFRA_DEFAULTS_FILE = "infra-defaults.properties";
    private static final String MODULE_CONFIG_FILE = "config.properties";

    private static volatile ConfigManager INSTANCE;

    private final Properties properties;

    private ConfigManager(Properties properties) {
        this.properties = properties;
    }

    /**
     * Returns the shared {@link ConfigManager}, initializing it on first access (thread-safe).
     */
    public static ConfigManager get() {
        if (INSTANCE == null) {
            synchronized (ConfigManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = init();
                }
            }
        }
        return INSTANCE;
    }

    private static ConfigManager init() {
        Properties props = new Properties();
        loadInto(props, INFRA_DEFAULTS_FILE);
        loadInto(props, MODULE_CONFIG_FILE);
        log.info("ConfigManager initialized (merged {} + {})", INFRA_DEFAULTS_FILE, MODULE_CONFIG_FILE);
        return new ConfigManager(props);
    }

    private static void loadInto(Properties target, String fileName) {
        try (InputStream in = ConfigManager.class.getClassLoader().getResourceAsStream(fileName)) {
            if (in == null) {
                log.warn("Config file '{}' not found on classpath, skipping.", fileName);
                return;
            }
            Properties chunk = new Properties();
            chunk.load(in);
            target.putAll(chunk);
            log.info("Loaded config from '{}'", fileName);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config file: " + fileName, e);
        }
    }

    /**
     * Validates that the given keys are present (in files or env vars).
     * Call this from your module's @BeforeSuite — not from infra.
     *
     * @throws IllegalStateException listing all missing keys at once
     */
    public void validateKeys(List<String> requiredKeys) {
        List<String> missing = new ArrayList<>();
        for (String key : requiredKeys) {
            String value = getString(key);
            if (value == null || value.isBlank()) {
                missing.add(key);
            }
        }
        if (!missing.isEmpty()) {
            throw new IllegalStateException(
                    "Missing mandatory config keys: " + missing
                            + ". Set them in config.properties or via the matching env vars."
            );
        }
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
