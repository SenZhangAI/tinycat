package com.zxx.tinycat.core.http.handler;

import com.zxx.tinycat.core.http.request.HttpRequest;
import com.zxx.tinycat.core.http.response.HttpResponse;

public interface HttpGeneralHandlerInterface {
    HttpResponse handle(HttpRequest request) throws Exception;
}
