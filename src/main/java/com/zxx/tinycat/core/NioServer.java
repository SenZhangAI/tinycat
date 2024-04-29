package com.zxx.tinycat.core;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class NioServer {

    public static void startServer(int port) throws IOException {
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
                    ByteBuffer byteBuffer = ByteBuffer.allocate(128);
                    int len = server.read(byteBuffer);
                    //如果有数据，把数据打印出来
                    if (len > 0) {
                        // 从写模式转换到读模式。其主要作用是准备缓冲区，以便数据可以被读取。
                        // 当在缓冲区写入数据后，position 指向下一个可写入的位置，而 limit 通常设置为缓冲区的容量。在调用 flip() 方法后，它会做两件事：
                        //1. 将limit 设置为当前的 position 值。这意味着标记了之前数据写入的位置，之后的位置不能再读取，因为那些位置没有有效数据。
                        //2. 将position设置为 0，这样从缓冲区的开始位置开始读取数据。
                        byteBuffer.flip();
                        // remaining方法--> 读模式下：在调用了 flip() 方法后，position 会设置为 0，而 limit 会设置为之前写入数据的位置。这时，remaining() 返回的是从 position 到 limit 的元素数量，即还可以从缓冲区中读取多少数据。
                        //             --> 写模式下: 在数据被写入缓冲区时，position 表示当前写入的位置，而 limit 通常设置为缓冲区的容量。这时，remaining() 返回的是从 position 到 limit 的元素数量，即缓冲区还能接受多少数据。
                        byte[] bytes = new byte[byteBuffer.remaining()];
                        byteBuffer.get(bytes);
                        String message = new String(bytes);
                        System.out.println("接收到消息：" + message);

                        String responseStr = "HelloWorld\n";
                        String responseData = "HTTP/1.1 200 OK\n" +
                                "Content-Length: " + responseStr.length() + "\n" +
                                "Content-Type: text/plain; charset=utf-8\n\n" + responseStr;
                        ByteBuffer writeBuffer = ByteBuffer.allocate(2048);
                        writeBuffer.put(responseData.getBytes());
                        writeBuffer.flip();
                        server.write(writeBuffer);
                    } else if (len == -1) {
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
}
