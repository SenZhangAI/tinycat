package com.zxx.tinycat.core.http.response;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {
    public static void main(String[] args) {
        HttpResponse httpResponse = new HttpResponse();
        httpResponse.setHttpVersion("Http/1.0");
        httpResponse.setCode(HttpResponseStatusCode.OK);
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");
        httpResponse.setHeaders(headers);
        String body = "true";
        httpResponse.setBody(body.getBytes());


    }

}