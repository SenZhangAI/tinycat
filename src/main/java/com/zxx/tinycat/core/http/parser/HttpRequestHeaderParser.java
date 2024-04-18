package com.zxx.tinycat.core.http.parser;

import com.sun.tools.javac.util.Assert;
import com.zxx.tinycat.core.HttpRequestStrReader;
import com.zxx.tinycat.core.http.HttpRequestReader;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpRequestHeaderParser {
    public static Map<String, String> parse(HttpRequestReader reader) throws Exception {
        Assert.check(reader.line() == 2 && reader.column() == 1, "解析请求头异常");

        HashMap<String, String> headers = new HashMap<>();
        while (!reader.isEmpty() && reader.peek() != '\n') {
            String headerKey = reader.consumeStr(':');
            reader.mustConsumeSpace();

            String headerValue = reader.consumeStr('\n');

            headers.put(headerKey, headerValue);
        }

        return headers;
    }

}
