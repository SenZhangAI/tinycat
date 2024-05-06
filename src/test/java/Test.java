import com.sun.xml.internal.ws.api.message.Packet;
import com.zxx.tinycat.core.NioServer;
import com.zxx.tinycat.core.http.request.HttpRequest;
import com.zxx.tinycat.core.http.request.HttpRequestHandler;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Test {
    public static void main(String[] args) throws Exception {
        String payload = "GET /user/list HTTP/1.1\n" +
                "User-Agent: Apifox/1.0.0 (https://apifox.com)\n" +
                "Accept: */*\n" +
                "Host: 127.0.0.1:8080\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Connection: keep-alive\n" +
                "\n";

        String payload2 = "POST /get/userId HTTP/1.1\n" +
                "User-Agent: Apifox/1.0.0 (https://apifox.com)\n" +
                "Content-Type: application/json\n" +
                "Accept: */*\n" +
                "Host: 127.0.0.1:8080\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 19\n" +
                "\n" +
                "{\n" +
                "    \"123\": true\n" +
                "}";


        String payload3 = "POST /get/userId HTTP/1.1\n" +
                "User-Agent: Apifox/1.0.0 (https://apifox.com)\n" +
                "Content-Type: application/json\n" +
                "Accept: */*\n" +
                "Host: 127.0.0.1:8080\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 19\n" +
                "\n" +
                "{\n" +
                "    \"123\": true\n" +
                "}";
        System.out.println(payload3.length());

        String payload4 = "GET / HTTP/1.1\n" +
                "User-Agent: Apifox/1.0.0 (https://apifox.com)\n" +
                "Accept: */*\n" +
                "Host: 127.0.0.1:8080\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Connection: keep-alive\n" +
                "\n";
    }



    /**
     * 模拟黏包的情况 第二个Http报文分成两段解析
     * @throws InterruptedException
     */
    @org.junit.jupiter.api.Test
    public  void stickyPacketTest() throws InterruptedException {
        HttpRequestHandler httpRequestHandler = new HttpRequestHandler();
        String payload1 = "GET /user/list HTTP/1.1\n" +
                "User-Agent: Apifox/1.0.0 (https://apifox.com)\n" +
                "Accept: */*\n" +
                "Host: 127.0.0.1:8080\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Connection: keep-alive\n" +
                "\n";

        String payload2 = "POST /get/userId HTTP/1.1\n" +
                "User-Agent: Apifox/1.0.0 (https://apifox.com)\n" +
                "Content-Type: application/json\n" +
                "Accept: */*\n" +
                "Host: 127.0.0";

        String payload3 = ".1:8080\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 19\n" +
                "\n" +
                "{\n" +
                "    \"123\": true\n" +
                "}";

        parsePayLoad(httpRequestHandler, payload1 + payload2, 1024);
        Thread.sleep(3000);
        System.out.println("等待3秒");


        parsePayLoad(httpRequestHandler, payload3, 1024);
    }

    //处理半包 把每次解析的buffer长度调小
    @org.junit.jupiter.api.Test
    public void partialPacketTest() {
        HttpRequestHandler httpRequestHandler = new HttpRequestHandler();
        String payload1 = "GET /user/list HTTP/1.1\n" +
                "User-Agent: Apifox/1.0.0 (https://apifox.com)\n" +
                "Accept: */*\n" +
                "Host: 127.0.0.1:8080\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Connection: keep-alive\n" +
                "\n";

        parsePayLoad(httpRequestHandler, payload1, 10);
    }

    @org.junit.jupiter.api.Test
    public void testErrorPayload() {

        String payload1 = "GET /user/list HTTP/1.1\n" +
                "User-Agent: Apifox/1.0.0 (https://apifox.com)\n" +
                "Accept: */*\n" +
                "Host: 127.0.0.1:8080\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Connection: keep-alive\n" +
                "\n";

        String errorPayload = "wejreshkwerjhkrewkhjjrl\n";

        String payload2 = "POST /get/userId HTTP/1.1\n" +
                "User-Agent: Apifox/1.0.0 (https://apifox.com)\n" +
                "Content-Type: application/json\n" +
                "Accept: */*\n" +
                "Host: 127.0.0.1:8080\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 19\n" +
                "\n" +
                "{\n" +
                "    \"123\": true\n" +
                "}";


        HttpRequestHandler httpRequestHandler = new HttpRequestHandler();


        parsePayLoad(httpRequestHandler, payload1 + errorPayload + payload2, 1024);
    }


    private static void parsePayLoad(HttpRequestHandler httpRequestHandler, String payload, int byteBufferSize) {
        Charset charset = StandardCharsets.UTF_8;

        ByteBuffer buffer = ByteBuffer.allocate(byteBufferSize);

        // 记录当前字符串的位置
        int position = 0;
        while (position < payload.length()) {
            int limit = Math.min(payload.length(), position + byteBufferSize);
            String part = payload.substring(position, limit);

            // 将字符串部分编码到ByteBuffer
            buffer.put(charset.encode(part));
            buffer.flip(); // 切换到读模式

            // 读取ByteBuffer中的内容
            while (buffer.hasRemaining()) {
                System.out.print((char) buffer.get());
            }

            NioServer.handleRequestSingleBuffer(buffer, httpRequestHandler);
            // 清空缓冲区并准备下一次写入
            buffer.clear();
            position += part.length(); // 更新位置
        }

        System.out.println("httpsRequest:size:" + httpRequestHandler.getHttpRequests().size());
        for (HttpRequest httpRequest : httpRequestHandler.getHttpRequests()) {
            System.out.println(httpRequest);
        }
        if (httpRequestHandler.getUnFinishRequestReader() != null) {
            System.out.println("exist unFinishPayload:" + httpRequestHandler.getUnFinishRequestReader().payload());
        }
        System.out.println("hhhhh");
    }

}
