package com.zxx.tinycat.core.http.request;

import com.zxx.tinycat.core.http.handler.HttpGeneralHandlerFactory;
import com.zxx.tinycat.core.http.response.HttpResponse;
import com.zxx.tinycat.core.http.response.HttpResponseStatusCode;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;


@Slf4j
public class HttpParseContext {
    private final static int BYTE_BUFFER_CAPACITY = 30;

    ByteBuffer byteBuffer;
    HttpRequestHandler httpRequestHandler;
    Long lastActiveTime;

    public HttpParseContext() {
        this.byteBuffer = ByteBuffer.allocate(BYTE_BUFFER_CAPACITY);
        this.httpRequestHandler = new HttpRequestHandler();
        lastActiveTime = System.currentTimeMillis();
    }

    public void refreshTime() {
        this.lastActiveTime = System.currentTimeMillis();
    }

    public void checkTimeouts(SocketChannel server) throws IOException {
        long currentTime = System.currentTimeMillis();

        if (currentTime - lastActiveTime > 30000) {
            sendResponse(server, new HttpResponse(HttpResponseStatusCode.REQUEST_TIMEOUT));
        }
    }

    public void parseRequest(SocketChannel server) throws IOException {
        int len = server.read(byteBuffer);

        //表明byteBuffer不够大,需要循环读取
        while (len == BYTE_BUFFER_CAPACITY) {
            handleRequestSingleBuffer();

            byteBuffer.clear();
            len = server.read(byteBuffer);
        }
        handleRequestSingleBuffer();

        //clear buffer
        byteBuffer.clear();
    }
    private void handleRequestSingleBuffer() {
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        httpRequestHandler.addPayload(new String(bytes));
    }


    public void handleResponse(SocketChannel server) throws Exception {
        if (httpRequestHandler.getParseFail() != null && httpRequestHandler.getParseFail()) {
            //返回 解析失败
            HttpResponse response = new HttpResponse(HttpResponseStatusCode.BAD_REQUEST);

            sendResponse(server, response);
            return;
        }
        for (HttpRequest httpRequest : httpRequestHandler.getHttpRequests()) {
            HttpGeneralHandlerFactory httpGeneralHandlerFactory = HttpGeneralHandlerFactory.getInstance();
            HttpResponse response = httpGeneralHandlerFactory.handle(httpRequest.getRequestMethod().getCode(), httpRequest.getUrl(), httpRequest);
            sendResponse(server, response);
            log.info("响应结束");
        }
        httpRequestHandler.clearHttpRequests();
    }




    private void sendResponse(SocketChannel server, HttpResponse response) throws IOException {
        String headerString = response.getResponseLineAndHeaderStr();
        byte[] headerBytes = headerString.getBytes(StandardCharsets.UTF_8);
        ByteBuffer headerBuffer = ByteBuffer.wrap(headerBytes);
        while (headerBuffer.hasRemaining()) {
            server.write(headerBuffer);
        }

        // 如果响应体不为空，则发送响应体
        if (response.getBody() != null) {
            ByteBuffer bodyBuffer = ByteBuffer.wrap(response.getBody());
            while (bodyBuffer.hasRemaining()) {
                server.write(bodyBuffer);
            }
        }

        // 刷新SocketChannel的输出缓冲区，确保所有数据都被发送
        server.socket().getOutputStream().flush();
    }



}
