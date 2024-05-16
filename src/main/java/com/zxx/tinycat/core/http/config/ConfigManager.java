package com.zxx.tinycat.core.http.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class ConfigManager {
    private static Properties properties = new Properties();

    static {
        loadConfig("config.properties");
    }

    private static void loadConfig(String configFilePath) {
        try (InputStream is = ConfigManager.class.getClassLoader().getResourceAsStream(configFilePath)) {
            if (is != null) {
                properties.load(is);
            } else {
                throw new RuntimeException("config.properties not found");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading config.properties", e);
        }
    }


    public static String getProperty(String key) {
        return properties.getProperty(key);
    }
}
