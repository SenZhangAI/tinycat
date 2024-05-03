package com.zxx.tinycat.core.http.request;

import com.zxx.tinycat.core.RequestMethodEnum;
import com.zxx.tinycat.core.http.exception.ErrorEnum;
import com.zxx.tinycat.core.http.exception.HttpParseException;
import com.zxx.tinycat.core.http.request.parser.HttpRequestBodyParser;
import com.zxx.tinycat.core.http.request.parser.HttpRequestHeaderParser;
import com.zxx.tinycat.core.http.request.parser.HttpRequestLineParser;
import com.zxx.tinycat.core.http.request.parser.HttpRequestReader;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 请求报文的结构图 <a href="https://segmentfault.com/img/bVbvbwo/view?w=770&h=324">...</a>
 */
public class HttpRequest {
    private String host;
    private String url;
    private Map<String, String> headers = new HashMap<>();
    private byte[] body;
    private RequestMethodEnum requestMethod;
    private String httpVersion;
    private int contentLength;


    public HttpRequest() {

    }


    void setHost(String host) {
        this.host = host;
    }

    void setUrl(String url) {
        this.url = url;
    }

    void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    void setBody(byte[] body) {
        this.body = body;
    }

    void setRequestMethod(RequestMethodEnum requestMethod) {
        this.requestMethod = requestMethod;
    }

    void setHttpVersion(String httpVersion) {
        this.httpVersion = httpVersion;
    }

    void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public String getHost() {
        return host;
    }

    public String getUrl() {
        return url;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public byte[] getBody() {
        return body;
    }

    public RequestMethodEnum getRequestMethod() {
        return requestMethod;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public int getContentLength() {
        return contentLength;
    }

    @Override
    public String toString() {
        return "HttpRequest{" +
                "host='" + host + '\'' +
                ", url='" + url + '\'' +
                ", headers=" + headers +
                ", body=" + Arrays.toString(body) +
                ", requestMethod=" + requestMethod +
                ", httpVersion='" + httpVersion + '\'' +
                ", contentLength=" + contentLength +
                '}';
    }


}
