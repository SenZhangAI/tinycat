package com.zxx.tinycat.core.http.request.parser;

//import com.sun.tools.javac.util.Assert;

import java.util.ArrayList;
import java.util.List;

public class HttpRequestLineParser {
    public static List<String> parse(HttpRequestReader reader) {
        List<String> requestLine = new ArrayList<>(3);

        String requestMethodStr = reader.consumeToEndChar(' ');
        String requestUrl = reader.consumeToEndChar(' ');
        String httpVersion = reader.consumeToLineEnd();

        requestLine.add(requestMethodStr);
        requestLine.add(requestUrl);
        requestLine.add(httpVersion);
        return requestLine;
    }
}
