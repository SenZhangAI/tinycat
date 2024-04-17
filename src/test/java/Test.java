import com.zxx.tinycat.core.Http;


public class Test {
    public static void main(String[] args) throws Exception {
        String payload = "GET /test/getUserId HTTP/1.1\n" +
                "User-Agent: PostmanRuntime/7.29.2\n" +
                "Accept: */*\n" +
                "Postman-Token: 7fb7f8e0-d7d0-4ebd-a052-f48ef48ebbf1\n" +
                "Host: 127.0.0.1:8081\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Connection: keep-alive";
        Http http = new Http(payload);
        System.out.println(http);

        String payload2 = "POST /test/getUserId HTTP/1.1\n" +
                "Content-Type: application/json\n" +
                "User-Agent: PostmanRuntime/7.29.2\n" +
                "Accept: */*\n" +
                "Postman-Token: 8ca63ca5-2143-4fb4-93d7-876ad279555e\n" +
                "Host: 127.0.0.1:8081\n" +
                "Accept-Encoding: gzip, deflate, br\n" +
                "Connection: keep-alive\n" +
                "Content-Length: 18\n" +
                "\n" +
                "{\n" +
                "    \"ads\": 123\n" +
                "}";
        Http http2 = new Http(payload2);
        System.out.println(http2);

    }
}
