package com.zxx.tinycat.core;

import com.zxx.tinycat.core.http.request.HttpRequest;
import com.zxx.tinycat.core.http.request.HttpRequestPool;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class NioServer {
    private final static int byteBufferCapacity = 32;

    public static void startServer(int port) throws Exception {
        //创建NIO ServerSocketChannel
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.socket().bind(new InetSocketAddress(port));
        //设置ServerSocketChannel为非阻塞
        serverSocketChannel.configureBlocking(false);
        //打开Selector处理Channel,即创建epoll
        Selector selector = Selector.open();
        //把ServerSocketChannel注册到selector上，并且selector对客户端accept连接操作感兴趣
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
        System.out.println("服务器启动成功");

        while (true) {
            //阻塞等待需要处理的事情发生 (注意是阻塞等待，如果没有注册的事件发生会处于休眠状态，不会占用CPU资源)
            selector.select();

            //获取selector中注册的全部时间的SelectionKey实例
            Set<SelectionKey> selectionKeys = selector.selectedKeys();
            Iterator<SelectionKey> iterator = selectionKeys.iterator();

            //遍历SelectionKey对事件进行处理
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                //如果是OP_ACCEPT事件，则进行连接获取和事件注册
                if (key.isAcceptable()) {
                    ServerSocketChannel server = (ServerSocketChannel) key.channel();
                    SocketChannel socketChannel = server.accept();
                    socketChannel.configureBlocking(false);
                    // 这里只注册了读事件，如果需要给客户端发送数据可以注册写事件
                    socketChannel.register(selector, SelectionKey.OP_READ);
                    System.out.println("客户端连接成功");
                } else if (key.isReadable()) {
                    //如果是OP_READ事件，则进行读取和打印
                    SocketChannel server = (SocketChannel) key.channel();
                    ByteBuffer byteBuffer = ByteBuffer.allocate(byteBufferCapacity);
                    int len = server.read(byteBuffer);

                    //表明byteBuffer不够大,需要循环读取
                    HttpRequestPool httpRequestPool = new HttpRequestPool();
                    while (len == byteBufferCapacity) {
                        handleRequestSingleBuffer(byteBuffer, httpRequestPool);

                        byteBuffer.clear();
                        len = server.read(byteBuffer);
                    }
                    handleRequestSingleBuffer(byteBuffer, httpRequestPool);

                    response(server, httpRequestPool);
                    if (len < 0) {
                        //如果客户端断开连接，关闭socket
                        System.out.println("客户端断开连接");
                        server.close();
                    }
                }
                //从事件集合里删除本次处理的key,防止下次select重复使用
                iterator.remove();
            }
        }
    }

    public static void handleRequestSingleBuffer(ByteBuffer byteBuffer, HttpRequestPool requestPool) {
        byteBuffer.flip();
        byte[] bytes = new byte[byteBuffer.remaining()];
        byteBuffer.get(bytes);
        System.out.println("接收到的请求为:" + new String(bytes));
        requestPool.addPayload(new String(bytes));
    }


    public static void response(SocketChannel server, HttpRequestPool httpRequestPool) throws IOException {
        if (httpRequestPool.getHttpRequests() == null || httpRequestPool.getHttpRequests().isEmpty()) {
            return;
        }
        for (HttpRequest httpRequest : httpRequestPool.getHttpRequests()) {
            System.out.println("--------http请求报文-----------");
            System.out.println(httpRequest.toString());

            String responseStr = "HelloWorld\n";
            String responseData = "HTTP/1.1 200 OK\n" +
                    "Content-Length: " + responseStr.length() + "\n" +
                    "Content-Type: text/plain; charset=utf-8\n\n" + responseStr;
            ByteBuffer writeBuffer = ByteBuffer.allocate(204800);
            writeBuffer.put(responseData.getBytes());
            writeBuffer.flip();
            server.write(writeBuffer);
            System.out.println("响应结束");
        }
    }
}
