package com.zxx.tinycat.core.http.config;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigManagerTest {
    @Test
    void getProperty() {
        String aa = ConfigManager.getProperty("tinyServerPort");
        System.out.println(aa);
    }

}