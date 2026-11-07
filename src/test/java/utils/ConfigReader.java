package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads config values (like LinkedIn credentials) from either an environment
 * variable or src/test/resources/config.properties. This way credentials
 * never have to be hardcoded or committed to source control.
 */
public final class ConfigReader {

    private static final Properties PROPERTIES = new Properties();

    static {
        try (InputStream in = ConfigReader.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in != null) {
                PROPERTIES.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    private ConfigReader() {
    }

    public static String get(String key) {
        String envValue = System.getenv(key);
        if (envValue != null && !envValue.isBlank()) {
            return envValue;
        }
        String propValue = PROPERTIES.getProperty(key);
        if (propValue != null && !propValue.isBlank()) {
            return propValue;
        }
        return null;
    }

    public static String require(String key) {
        String value = get(key);
        if (value == null) {
            throw new IllegalStateException(
                    "Missing required config value '" + key + "'. Set it as an environment variable "
                            + "or in src/test/resources/config.properties (see README.md).");
        }
        return value;
    }

    public static String getOrDefault(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }
}
