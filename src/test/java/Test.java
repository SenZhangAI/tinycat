import com.zxx.tinycat.core.http.request.HttpRequest;

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
}
