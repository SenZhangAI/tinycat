package com.zxx.tinycat.example;

import com.zxx.tinycat.core.http.handler.HttpGeneralHandlerInterface;
import com.zxx.tinycat.core.http.request.HttpRequest;
import com.zxx.tinycat.core.http.response.HttpResponse;
import com.zxx.tinycat.core.http.response.HttpResponseStatusCode;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OrderDynamicHandler implements HttpGeneralHandlerInterface {
    @Override
    public HttpResponse handle(HttpRequest request)  {
        request.getParams().forEach((key, value) -> {
            log.info("key:{},value:{}", key, value);
        });
        String result = "{\"orderId\": \"123\"}";
        HttpResponse response = new HttpResponse();
        response.setCode(HttpResponseStatusCode.OK);
        response.addHeader("Content-Type", "application/json");
        response.setBody(result.getBytes());
        response.addHeader("Content-Length", String.valueOf(result.length()));

        return response;
    }

}
