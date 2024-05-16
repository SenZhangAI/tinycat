package com.zxx.tinycat.core.http.handler;

import com.zxx.tinycat.core.http.request.HttpRequest;
import com.zxx.tinycat.core.http.response.HttpResponse;
import com.zxx.tinycat.core.http.response.HttpResponseStatusCode;
import com.zxx.tinycat.core.http.util.StringUtil;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpGeneralHandlerFactory {
    private static HttpGeneralHandlerFactory instance = null;
    // key 例如  /api/v1/user/list value: 对应的handler
    static Map<String, HttpGeneralHandlerInterface> handlerMap = new HashMap<>();
    //restful 风格
    static RestfulPathTreeRoot restRoot = new RestfulPathTreeRoot();
    static List<Pair<String, HttpGeneralHandlerInterface>> staticFileHandler = new ArrayList<>();

    public void register(String method, String url, HttpGeneralHandlerInterface handler) throws Exception {
        restRoot.registerNode(method, url, handler);

        handlerMap.put(method + url, handler);
    }

    public void registerFileHandler(String prefix, String rootDir) {
        final HttpStaticFileHandler httpStaticFileHandler = new HttpStaticFileHandler(rootDir);
        final Pair<String, HttpGeneralHandlerInterface> p = new Pair<>(prefix, httpStaticFileHandler);
        staticFileHandler.add(p);
    }

    HttpGeneralHandlerInterface searchHandler(String method, String url) {
        //尝试获取静态资源
        if ("GET".equals(method)) {
            final HttpGeneralHandlerInterface fileHandler = searchStaticFileHandler(url);
            if (fileHandler != null) {
                return fileHandler;
            }
        }
        //动态请求
        final HttpGeneralHandlerInterface dynamicHandler = handlerMap.get(method + url);
        if (dynamicHandler != null) {
            return dynamicHandler;
        }


        Map<String, String> restParamMap = new HashMap<>();
        final HttpGeneralHandlerInterface search = restRoot.search(method, url, restParamMap);
        if (search != null) {
            //step1. restParamMap 放到 request 中;
            return search;
        }
        return null;
    }

    HttpGeneralHandlerInterface searchStaticFileHandler(String url) {
        for (final Pair<String, HttpGeneralHandlerInterface> pair : staticFileHandler) {
            if (StringUtil.beginWith(url, pair.getKey())) {
                return pair.getValue();
            }
        }
        return null;
    }

    public HttpResponse handle(String method, String url, HttpRequest request) throws Exception {
        final HttpGeneralHandlerInterface handler = searchHandler(method, url);
        if (handler != null) {
            final HttpResponse response = handler.handle(request);
            return response;
        } else {
            // response 404;
            HttpResponse response = new HttpResponse(HttpResponseStatusCode.NOT_FOUND);
            return response;
        }
    }


    private HttpGeneralHandlerFactory() {

    }

    public static HttpGeneralHandlerFactory getInstance() {
        if (instance == null) {
            synchronized (HttpGeneralHandlerFactory.class) {
                // 再次检查实例是否存在，避免多线程问题
                if (instance == null) {
                    instance = new HttpGeneralHandlerFactory();
                }
            }
        }
        return instance;
    }
}
