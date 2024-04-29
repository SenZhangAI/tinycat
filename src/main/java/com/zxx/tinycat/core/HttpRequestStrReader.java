package com.zxx.tinycat.core;

public class HttpRequestStrReader {
    final String payload;
    final int length;
    int pos;
    int line;
    int column;

    public HttpRequestStrReader(String payload) {
       this.payload = payload;
       this.length = payload.length();
       this.pos = 0;
       this.line = 1;
       this.column = 1;
    }





}
