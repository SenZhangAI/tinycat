package com.zxx.tinycat.core.http.request.parser;

import java.util.HashMap;
import java.util.Map;

public class HttpRequestHeaderParser {
    public static Map<String, String> parse(HttpRequestReader reader) {

        HashMap<String, String> headers = new HashMap<>();
        while (!(reader.isEmpty() || reader.peek() == '\n' || (reader.peek() == '\r' && reader.peekNextChar() == '\n'))) {
            String headerKey = reader.consumeToEndChar(':');
            reader.mustConsumeSpace();

            String headerValue = reader.consumeToLineEnd();

            headers.put(headerKey, headerValue);
        }
        if (reader.isNotEmpty()) {
            reader.consumeNewLine();
        }

        return headers;
    }

}
