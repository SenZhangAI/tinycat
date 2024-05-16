package com.zxx.tinycat.core.http.request;


import com.zxx.tinycat.core.http.exception.ErrorEnum;
import com.zxx.tinycat.core.http.exception.HttpParseException;
import com.zxx.tinycat.core.http.request.parser.HttpRequestBodyParser;
import com.zxx.tinycat.core.http.request.parser.HttpRequestHeaderParser;
import com.zxx.tinycat.core.http.request.parser.HttpRequestLineParser;
import com.zxx.tinycat.core.http.request.parser.HttpRequestReader;
import jdk.jfr.internal.instrument.ThrowableTracer;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 为了处理粘包和半包的问题, 引入HttpRequestHandler, Stream流获取到的payload可能存在 完整的请求报文/多个完整Http请求报文/部分Http请求报文/异常的请求报文等情况
 * unParsedPayload 存放前面未解析完的数据
 *
 */
@Data
public class HttpRequestHandler {
    private List<String> payloads = new ArrayList<>();

    private List<HttpRequest> httpRequests = new ArrayList<>();

    private HttpRequestReader unFinishRequestReader;

    private Boolean parseFail;



    public void addPayload(String payload) {
        if (payload == null || payload.isEmpty()) {
            return;
        }
        this.payloads.add(payload);

        if (this.unFinishRequestReader != null) {
            this.unFinishRequestReader.appendPayLoad(payload);
        } else {
            this.unFinishRequestReader = new HttpRequestReader(payload);
        }

        try {
            parseUnFinishRequestReader();
        } catch (HttpParseException e) {
            if (e.getErrorCode() == ErrorEnum.HTTP_REQUEST_PARSE_FAIL.getCode()) {
                this.parseFail = true;
            } else if (e.getErrorCode() == ErrorEnum.HTTP_REQUEST_PARSE_UNFINISHED.getCode()) {
                //未解析完成属于正常现象
                unFinishRequestReader.freshPos();
            }
        } catch (Throwable e) {
            this.parseFail = true;
        }
    }

    public void clearHttpRequests() {
        this.httpRequests.clear();
    }
    private void parseUnFinishRequestReader() {
        List<String> requestLine = HttpRequestLineParser.parse(unFinishRequestReader);
        HttpRequest httpRequest = new HttpRequest();
        httpRequest.setRequestMethod(RequestMethodEnum.valueOf(requestLine.get(0)));
        httpRequest.setUrl(requestLine.get(1));
        httpRequest.setHttpVersion(requestLine.get(2));
        httpRequest.setHeaders(HttpRequestHeaderParser.parse(unFinishRequestReader));
        httpRequest.setHost(httpRequest.getHeaders().get("Host"));

        int contentLength = Integer.parseInt(httpRequest.getHeaders().getOrDefault("Content-Length", "0"));
        httpRequest.setContentLength(contentLength);
        if (httpRequest.getContentLength() > 0) {
            httpRequest.setBody(HttpRequestBodyParser.parse(unFinishRequestReader, contentLength));
        }
        httpRequests.add(httpRequest);

        if (unFinishRequestReader.isEmpty()) {
            unFinishRequestReader = null;
        } else {
            //解析出一个HttpRequest 继续解析剩余字符串
            unFinishRequestReader = new HttpRequestReader(unFinishRequestReader.remainStr());
            parseUnFinishRequestReader();
        }
    }
}