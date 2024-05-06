package com.zxx.tinycat.core.http.request;


import com.zxx.tinycat.core.RequestMethodEnum;
import com.zxx.tinycat.core.http.exception.ErrorEnum;
import com.zxx.tinycat.core.http.exception.HttpParseException;
import com.zxx.tinycat.core.http.request.parser.HttpRequestBodyParser;
import com.zxx.tinycat.core.http.request.parser.HttpRequestHeaderParser;
import com.zxx.tinycat.core.http.request.parser.HttpRequestLineParser;
import com.zxx.tinycat.core.http.request.parser.HttpRequestReader;

import java.util.ArrayList;
import java.util.List;

/**
 * 为了处理粘包和半包的问题, 引入HttpRequestPool, Stream流获取到的payload可能存在 完整的请求报文/多个完整Http请求报文/部分Http请求报文/异常的请求报文等情况
 * unParsedPayload 存放前面未解析完的数据
 *
 */
public class HttpRequestHandler {
    private List<String> payloads = new ArrayList<>();

    private List<HttpRequest> httpRequests = new ArrayList<>();

    private HttpRequestReader unFinishRequestReader;

    public List<HttpRequest> getHttpRequests() {
        return this.httpRequests;
    }

    public HttpRequestReader getUnFinishRequestReader() {
        return unFinishRequestReader;
    }

    public void addPayload(String payload) {
        if (payload == null || payload.isEmpty()) {
            throw new IllegalArgumentException("payload is null or empty");
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
                unFinishRequestReader.freshPos();
                unFinishRequestReader.setFail(true);
            } else if (e.getErrorCode() == ErrorEnum.HTTP_REQUEST_PARSE_UNFINISHED.getCode()) {
                //未解析完成属于正常现象
                unFinishRequestReader.freshPos();
            }
        }
    }

    private void parseUnFinishRequestReader() {
        if (unFinishRequestReader.getFail() != null && unFinishRequestReader.getFail()) {
            //如果存在解析失败的，需要找到下一个Http请求报文的请求头,把这些解析失败的str剔除掉 避免影响后面的Http报文的解析
            //如果服务器接收到 A:格式正确的Http请求  B:格式错误的Http请求 C:格式正确的Http请求   确保B不会影响到C的解析
            int nextHttpLinePos = tryToFindNextHttpLine();
            if (nextHttpLinePos > 0) {
                unFinishRequestReader.consumePos(nextHttpLinePos - 1);
            }
        }
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
    private int tryToFindNextHttpLine() {
        HttpRequestReader copyRequestReader = new HttpRequestReader(unFinishRequestReader.payload());

        int tryToFindCount = 3;
        while (copyRequestReader.isNotEmpty() && tryToFindCount > 0) {
            try {
                int beginLineCharPos = copyRequestReader.pos();
                HttpRequestLineParser.parse(copyRequestReader);

                return beginLineCharPos;
            } catch (Exception e) {
                tryToFindCount --;
                copyRequestReader.consumeToLineEnd();
            }
        }
        return -1;
    }
}