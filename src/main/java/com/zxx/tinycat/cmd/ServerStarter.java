package com.zxx.tinycat.cmd;

import com.zxx.tinycat.core.TinyCat;
import com.zxx.tinycat.core.http.config.ConfigManager;
import com.zxx.tinycat.example.OrderDynamicHandler;
import com.zxx.tinycat.example.TradeDynamicHandler;
import com.zxx.tinycat.example.UserDynamicHandler;


public class ServerStarter {
    public static void main(String[] args) throws Exception {
        TinyCat tinyCat = new TinyCat();
        tinyCat.staticFile("/static/", ConfigManager.getProperty("documentRoot"));
        tinyCat.staticFile("/static/subdir1/", ConfigManager.getProperty("documentRoot"));

        tinyCat.get("/user/info", new UserDynamicHandler());
        tinyCat.get("/order/{orderId}/list", new OrderDynamicHandler());
        tinyCat.get("/trade/{tradeId}/user/{userId}/list", new TradeDynamicHandler());

        tinyCat.run();
    }
}
