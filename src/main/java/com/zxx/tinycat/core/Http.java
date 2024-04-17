package com.zxx.tinycat.core;

import com.sun.tools.javac.util.Assert;

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
    private String HttpVersion;

    public Http(String payload) throws Exception {
        if (payload == null) {
            throw new Exception("非法报文格式,payload:" + payload);
        }
        String firstLine = payload.substring(0, payload.indexOf("\n"));
        //Http 报文的第一部分(也就是第一行)   结构:请求方式 统一资源标识符 Http版本协议
        List<String> firstLineList = Arrays.asList(firstLine.split(" "));
        Assert.check(firstLineList.size() == 3, "非法格式报文");
        this.requestMethod = RequestMethodEnum.valueOf(firstLineList.get(0));
        this.url = firstLineList.get(1);
        this.HttpVersion = firstLineList.get(2);

        String headAndBodyPartStr = payload.substring(payload.indexOf("\n") + 1);
        List<String> headAndBodyPartList = Arrays.asList(headAndBodyPartStr.split("\n\n"));
        //Http 报文的第二部分(也就是请求头)  结构: key: 值 \n
        String headerPart = headAndBodyPartList.get(0);
        parseHeaders(headerPart);

        //Http 报文的第三部分(也就是请求体) 结构: json对象
        if (headAndBodyPartList.size() == 2) {
            String requestBodyPart = headAndBodyPartList.get(1);
            parseRequestBody(requestBodyPart);
        }
    }

    private void parseHeaders(String headerPart) {
        List<String> headerList = Arrays.asList(headerPart.split("\n"));
        for (String header : headerList) {
            String[] split = header.split(": ");
            String headKey = split[0];
            String headValue = split[1];
            if ("Host".equalsIgnoreCase(headKey)) {
                this.host = headValue;
            }
            this.headers.put(headKey, headValue);
        }
    }

    private void parseRequestBody(String requestBodyPart) throws Exception {
        if (requestBodyPart == null) {
            throw new Exception("非法格式报文");
        }
        if (requestBodyPart.trim().length() == 0) {
            return;
        }
        this.body = requestBodyPart.getBytes();
    }

    @Override
    public String toString() {
        return "Http{" +
                "host='" + host + '\'' +
                ", url='" + url + '\'' +
                ", headers=" + headers +
                ", body=" + Arrays.toString(body) +
                ", requestMethod=" + requestMethod +
                ", HttpVersion='" + HttpVersion + '\'' +
                '}';
    }
}
