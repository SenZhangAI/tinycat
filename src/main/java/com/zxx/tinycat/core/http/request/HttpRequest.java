package com.zxx.tinycat.core.http.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
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

    private Map<String, Object> params = null;


    public HttpRequest() {

    }

    public Map<String, Object> getParams() {
        //这里采用Lazy方式， 只有在需要使用params的时候再尝试解析
        if (params == null) {
            setParamsFromUrl();
        }
        return params;
    }

    public void setParamsFromUrl() {
        if (!url.contains("?")) {
            return;
        }

        HashMap<String, Object> paramsMap = new HashMap<>();
        //这里不用URL类获取值, 单纯地解析字符串当练手
        String paramStr = url.split("\\?")[1];
        new ArrayList<>(Arrays.asList(paramStr.split("&"))).forEach(s -> {
            String[] split = s.split("=");
            paramsMap.put(split[0], split[1]);
        });

        setParams(paramsMap);
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }
    public void addParams(Map<String, Object> params) {
        if (this.params == null) {
            this.params = new HashMap<>();
        }
        this.params.putAll(params);
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
