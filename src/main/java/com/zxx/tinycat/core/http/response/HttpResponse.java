package com.zxx.tinycat.core.http.response;

import lombok.Data;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Http响应报文的结构图 <a href="https://images2017.cnblogs.com/blog/1191499/201802/1191499-20180215171730312-494108816.png">...</a>
 */
@Data
public class HttpResponse {
    private Map<String, String> headers = new HashMap<>();
    private HttpResponseStatusCode code;
    private String httpVersion = "HTTP/1.1";
    private int contentLength;
    private byte[] body;

    private String serverName;


    public HttpResponse() {
    }
    public HttpResponse(HttpResponseStatusCode code) {
        this.code = code;
    }

    private String getResponseLine() {
        return httpVersion + " " + code.getCode() + " " + code.getDescription() + "\n";
    }
    private String getResponseHeaderStr() {
        StringBuilder sb = new StringBuilder();
        for (String key : headers.keySet()) {
            sb.append(key + ": " + headers.get(key) + "\n");
        }
        return sb.toString();
    }

    public String getResponseLineAndHeaderStr() {
        return getResponseLine() + getResponseHeaderStr() + "\r\n";
    }

    public void addHeader(String headerKey, String contentType) {
        headers.put(headerKey, contentType);
    }
}
