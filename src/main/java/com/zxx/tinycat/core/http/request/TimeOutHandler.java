package com.zxx.tinycat.core.http.request;

import java.util.logging.FileHandler;

public class TimeOutHandler {
    private long startTime;
    private long timeoutMillis;

    public TimeOutHandler(long timeoutMillis) {
        this.timeoutMillis = timeoutMillis;
        this.startTime = System.currentTimeMillis();
    }

    public TimeOutHandler() {
        this.timeoutMillis = 6000;
        this.startTime = System.currentTimeMillis();
    }

    public boolean checkTimeout() {
        return (System.currentTimeMillis() - startTime) > timeoutMillis;
    }
}