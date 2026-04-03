package com.multibank.config;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Slf4j
public final class EnvironmentConfig {

    private static final Properties PROPS = new Properties();

    static {
        String env = System.getProperty("env", "prod");
        String file = "config/" + env + ".properties";
        try (InputStream is = EnvironmentConfig.class.getClassLoader().getResourceAsStream(file)) {
            if (is != null) {
                PROPS.load(is);
                log.info("Loaded environment config: {}", file);
            } else {
                log.warn("Config file not found on classpath: {} – relying on system properties", file);
            }
        } catch (IOException e) {
            log.error("Failed to read config file: {}", file, e);
        }
    }

    private EnvironmentConfig() {}

    /** Returns the value for {@code key}, checking system props first. */
    public static String get(String key) {
        // Explicit -D override wins
        String sysProp = System.getProperty(key);
        if (sysProp != null && !sysProp.isBlank()) return sysProp;

        // Then env variable (e.g. BASE_URL)
        String envVar = System.getenv(key.toUpperCase().replace('.', '_'));
        if (envVar != null && !envVar.isBlank()) return envVar;

        // Finally file
        return PROPS.getProperty(key, "");
    }

    public static String get(String key, String defaultValue) {
        String value = get(key);
        return (value == null || value.isBlank()) ? defaultValue : value;
    }

    public static int getInt(String key, int defaultValue) {
        String value = get(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        String value = get(key);
        if (value == null || value.isBlank()) return defaultValue;
        return Boolean.parseBoolean(value);
    }
}
