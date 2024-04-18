package com.zxx.tinycat.core.http.parser;

import com.sun.tools.javac.util.Assert;
import com.zxx.tinycat.core.http.HttpRequestReader;

import java.util.Map;

public class HttpRequestBodyParser {
    public static byte[] parse(HttpRequestReader reader) {
        if (reader.isEmpty()) {
            return null;
        }

        return reader.payload().substring(reader.pos()).getBytes();
    }

}
