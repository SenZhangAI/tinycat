package com.zxx.tinycat.example;

import com.zxx.tinycat.core.http.handler.HttpGeneralHandlerInterface;
import com.zxx.tinycat.core.http.request.HttpRequest;
import com.zxx.tinycat.core.http.response.HttpResponse;
import com.zxx.tinycat.core.http.response.HttpResponseStatusCode;

public class OrderDynamicHandler implements HttpGeneralHandlerInterface {
    @Override
    public HttpResponse handle(HttpRequest request) throws Exception {
        //TODO 传参
        String result = "{\"orderId\": \"123\"}";
        HttpResponse response = new HttpResponse();
        response.setCode(HttpResponseStatusCode.OK);
        response.addHeader("Content-Type", "application/json");
        response.setBody(result.getBytes());
        response.addHeader("Content-Length", String.valueOf(result.length()));

        return response;
    }

}
