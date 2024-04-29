package com.zxx.tinycat.core.http.request.parser;

import com.sun.tools.javac.util.Assert;
import com.zxx.tinycat.core.http.request.HttpRequestReader;

public class HttpRequestBodyParser {
    public static byte[] parse(HttpRequestReader reader) {
        if (reader.isEmpty()) {
            return null;
        }

        return reader.payload().substring(reader.pos()).getBytes();
    }

}
