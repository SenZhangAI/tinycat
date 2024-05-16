package com.zxx.tinycat.core;

import com.zxx.tinycat.core.http.config.ConfigManager;
import com.zxx.tinycat.core.http.handler.HttpGeneralHandlerFactory;
import com.zxx.tinycat.core.http.handler.HttpGeneralHandlerInterface;
import com.zxx.tinycat.core.http.request.RequestMethodEnum;

public class TinyCat {
    private NioServer nioServer = new NioServer();
    private HttpGeneralHandlerFactory httpGeneralHandlerFactory = HttpGeneralHandlerFactory.getInstance();


    public void run() throws Exception {
        nioServer.startServer(Integer.parseInt(ConfigManager.getProperty("tinyServerPort")));
    }

    public void staticFile(String prefix, String rootDir) {
        httpGeneralHandlerFactory.registerFileHandler(prefix, rootDir);
    }

    public void get(String url, HttpGeneralHandlerInterface handler) throws Exception {
        httpGeneralHandlerFactory.register(RequestMethodEnum.GET.getCode(), url, handler);
    }
    public void post(String url, HttpGeneralHandlerInterface handler) throws Exception {
        httpGeneralHandlerFactory.register(RequestMethodEnum.POST.getCode(), url, handler);
    }
    public void put(String url, HttpGeneralHandlerInterface handler) throws Exception {
        httpGeneralHandlerFactory.register(RequestMethodEnum.PUT.getCode(), url, handler);
    }
}
