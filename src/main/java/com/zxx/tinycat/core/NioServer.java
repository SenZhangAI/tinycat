package com.zxx.tinycat.core;

import com.zxx.tinycat.core.http.request.HttpRequest;
import com.zxx.tinycat.core.http.request.HttpRequestHandler;
import com.zxx.tinycat.core.http.response.HttpResponse;
import com.zxx.tinycat.core.http.response.HttpResponseStatusCode;
import com.zxx.tinycat.core.http.handler.HttpGeneralHandlerFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class NioServer {
    private final static int byteBufferCapacity = 30;
    private final static int requestTimeoutMillis = 30000;
    private Map<SocketChannel, HttpRequestHandler> requestHandlerMap = new ConcurrentHashMap<>();
    private Map<SocketChannel, Long> lastActivityTime = new HashMap<>();


    // 创建一个固定大小的线程池
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
            20,
            300,
            100,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(200)
    );


    public void startServer(int port) throws Exception {
        //创建NIO ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        //设置ServerSocketChannel为非阻塞
        serverSocketChannel.configureBlocking(false);
        //打开Selector处理Channel,即创建epoll
        Selector selector = Selector.open();
        //把ServerSocketChannel注册到selector上，并且selector对客户端accept连接操作感兴趣
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("服务器启动成功");

        while (true) {
            //阻塞等待需要处理的事情发生 (注意是阻塞等待，如果没有注册的事件发生会处于休眠状态，不会占用CPU资源)
            selector.select();

            //获取selector中注册的全部时间的SelectionKey实例(包含了Selector所知道的所有准备就绪的事件的SelectionKey集合,这个集合代表了所有已经准备好进行I/O操作的通道)
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            //遍历SelectionKey对事件进行处理
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                //如果是OP_ACCEPT事件，则进行连接获取和事件注册
                if (key.isAcceptable()) {
                    doAcceptEvent(selector, key);
                } else if (key.isReadable()) {
                    //TODO 异步处理
                    doReadEvent(key);
                }


                //从事件集合里删除本次处理的key,防止下次select重复使用(如果不移除已处理的键，那么下次循环时selectedKeys()还会包含这些键，您的程序可能会错误地再次处理它们，即使它们并没有新的I/O事件发生。)
                iterator.remove();

                //超时处理
                checkTimeouts();
            }
        }
    }


    private void doAcceptEvent(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = server.accept();
        socketChannel.configureBlocking(false);
        // 这里只注册了读事件，如果需要给客户端发送数据可以注册写事件
        socketChannel.register(selector, SelectionKey.OP_READ);
        log.info("客户端连接成功");
    }

    private void doReadEvent(SelectionKey key) throws Exception {
        //如果是OP_READ事件，则进行读取
        SocketChannel server = (SocketChannel) key.channel();

        HttpRequestHandler httpRequestHandler;
        if (requestHandlerMap.containsKey(server)) {
            httpRequestHandler = requestHandlerMap.get(server);
        } else {
            httpRequestHandler = new HttpRequestHandler();
            requestHandlerMap.put(server, httpRequestHandler);
        }


        lastActivityTime.put(server, System.currentTimeMillis());

        ByteBuffer byteBuffer = ByteBuffer.allocate(byteBufferCapacity);
        int len = server.read(byteBuffer);

        //表明byteBuffer不够大,需要循环读取
        while (len == byteBufferCapacity) {
            handleRequestSingleBuffer(byteBuffer, httpRequestHandler);

            byteBuffer.clear();
            len = server.read(byteBuffer);
        }
        handleRequestSingleBuffer(byteBuffer, httpRequestHandler);

        handleResponse(server, httpRequestHandler);

        closeConnection(server);
    }

    private void checkTimeouts() {
        long currentTime = System.currentTimeMillis();
        Iterator<Map.Entry<SocketChannel, Long>> it = lastActivityTime.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<SocketChannel, Long> entry = it.next();
            if (currentTime - entry.getValue() > requestTimeoutMillis) {
                try {
                    sendResponse(entry.getKey(), new HttpResponse(HttpResponseStatusCode.REQUEST_TIMEOUT));
                    entry.getKey().close();
                } catch (IOException e) {
                    System.err.println("Failed to close channel: " + e.getMessage());
                }
                it.remove(); // Remove the entry after closing the channel
            }
        }
    }


    private void closeConnection(SocketChannel server) throws IOException {
        log.info("客户端断开连接");
        requestHandlerMap.remove(server);
        lastActivityTime.remove(server);
        server.close();
    }

    public void handleRequestSingleBuffer(ByteBuffer byteBuffer, HttpRequestHandler requestHandler) {
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        requestHandler.addPayload(new String(bytes));
    }


    public void handleResponse(SocketChannel server, HttpRequestHandler httpRequestHandler) throws Exception {
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

    private void sendResponse(SocketChannel server, String responseData) throws IOException {
        ByteBuffer writeBuffer = ByteBuffer.allocate(2048);
        writeBuffer.put(responseData.getBytes());
        writeBuffer.flip();
        server.write(writeBuffer);
    }


}
