package com.zxx.tinycat.core.http;

import com.sun.tools.javac.util.Assert;
import com.zxx.tinycat.core.RequestMethodEnum;
import com.zxx.tinycat.core.http.parser.HttpRequestBodyParser;
import com.zxx.tinycat.core.http.parser.HttpRequestHeaderParser;
import com.zxx.tinycat.core.http.parser.HttpRequestLineParser;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求报文的结构图 https://segmentfault.com/img/bVbvbwo/view?w=770&h=324
 */
public class Http {
    private String host;
    private String url;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body;
    private RequestMethodEnum requestMethod;
    private String httpVersion;

    public Http(String payload) throws Exception {
        HttpRequestReader reader = new HttpRequestReader(payload);

        List<String> requestLine = HttpRequestLineParser.parse(reader);
        this.requestMethod = RequestMethodEnum.valueOf(requestLine.get(0));
        this.url = requestLine.get(1);
        this.httpVersion = requestLine.get(2);

        this.headers = HttpRequestHeaderParser.parse(reader);
        this.host = headers.get("Host");

        this.body = HttpRequestBodyParser.parse(reader);
    }


    @Override
    public String toString() {
        return "Http{" +
                "host='" + host + '\'' +
                ", url='" + url + '\'' +
                ", headers=" + headers +
                ", body=" + body +
                ", requestMethod=" + requestMethod +
                ", httpVersion='" + httpVersion + '\'' +
                '}';
    }
}
