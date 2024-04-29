package com.zxx.tinycat.core.http.request.parser;

import com.sun.tools.javac.util.Assert;
import com.zxx.tinycat.core.http.request.HttpRequestReader;

import java.util.ArrayList;
import java.util.List;

public class HttpRequestLineParser {
    public static List<String> parse(HttpRequestReader reader) throws Exception {
        Assert.check(reader.line() == 1 && reader.column() == 1 && reader.pos() == 0, "解析请求行异常");

        List<String> requestLine = new ArrayList<>(3);

        String requestMethodStr = reader.consumeStr(' ');
        String requestUrl = reader.consumeStr(' ');
        String httpVersion = reader.consumeStr('\n');

        requestLine.add(requestMethodStr);
        requestLine.add(requestUrl);
        requestLine.add(httpVersion);
        return requestLine;
    }
}
