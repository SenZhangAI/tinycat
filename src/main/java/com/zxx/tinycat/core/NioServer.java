package com.zxx.tinycat.core;

import com.zxx.tinycat.core.http.request.HttpParseContext;
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
    private final Map<SocketChannel, HttpParseContext> parseContextMap = new ConcurrentHashMap<>();


    // 创建一个固定大小的线程池
    ThreadPoolExecutor executor = new ThreadPoolExecutor(
            20,
            300,
            100,
            TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(200)
    );


    public void startServer(int port) throws Exception {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        serverSocketChannel.configureBlocking(false);
        Selector selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        log.info("服务器启动成功");

        while (true) {
            //阻塞等待需要处理的事情发生 (注意是阻塞等待，如果没有注册的事件发生会处于休眠状态，不会占用CPU资源)
            selector.select();

            //包含了Selector所知道的所有准备就绪的事件的SelectionKey集合,这个集合代表了所有已经准备好进行I/O操作的通道
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();

                if (key.isAcceptable()) {
                    doAcceptEvent(selector, key);
                } else if (key.isReadable()) {
                    doReadEvent(key);
                }

                iterator.remove();
            }
        }
    }


    private void doAcceptEvent(Selector selector, SelectionKey key) throws IOException {
        ServerSocketChannel server = (ServerSocketChannel) key.channel();
        SocketChannel socketChannel = server.accept();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        log.info("客户端连接成功");
    }

    private void doReadEvent(SelectionKey key) throws Exception {
        SocketChannel server = (SocketChannel) key.channel();
        parseContextMap.putIfAbsent(server, new HttpParseContext());

        HttpParseContext httpParseContext = parseContextMap.get(server);
        httpParseContext.refreshTime();
        httpParseContext.parseRequest(server);
        httpParseContext.checkTimeouts(server);
        httpParseContext.handleResponse(server);

        closeConnection(server);
    }


    private void closeConnection(SocketChannel server) throws IOException {
        log.info("客户端断开连接");
        parseContextMap.remove(server);
        server.close();
    }
}
