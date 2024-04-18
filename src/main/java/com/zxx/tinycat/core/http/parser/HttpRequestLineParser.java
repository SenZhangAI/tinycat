package com.zxx.tinycat.core.http.parser;

import com.sun.istack.internal.NotNull;
import com.sun.tools.javac.util.Assert;
import com.zxx.tinycat.core.HttpRequestStrReader;
import com.zxx.tinycat.core.http.HttpRequestReader;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
