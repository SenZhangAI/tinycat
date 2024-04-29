package com.zxx.tinycat.core.http.request.parser;

import com.zxx.tinycat.core.http.request.HttpRequestReader;

import java.util.HashMap;
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
