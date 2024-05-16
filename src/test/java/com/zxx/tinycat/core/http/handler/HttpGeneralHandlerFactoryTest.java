package com.zxx.tinycat.core.http.handler;

import com.zxx.tinycat.core.http.config.ConfigManager;
import org.junit.jupiter.api.Test;

class HttpGeneralHandlerFactoryTest {
    @Test
    void registerFileHandler() {
        HttpGeneralHandlerFactory httpGeneralHandlerFactory = HttpGeneralHandlerFactory.getInstance();

        httpGeneralHandlerFactory.registerFileHandler("/static/", ConfigManager.getProperty("documentRoot"));
        httpGeneralHandlerFactory.registerFileHandler("/static/subdir1", ConfigManager.getProperty("documentRoot"));
    }

}