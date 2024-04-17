package com.zxx.tinycat.cmd;

import com.zxx.tinycat.core.NioServer;

import java.io.IOException;

public class ServerStarter {
    public static void main(String[] args) throws IOException {
        NioServer.startServer(Integer.parseInt(System.getProperty("tinyServerPort", "8080")));
    }
}
