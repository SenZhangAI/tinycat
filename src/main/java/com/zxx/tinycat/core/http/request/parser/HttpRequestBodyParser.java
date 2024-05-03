package com.zxx.tinycat.core.http.request.parser;

//import com.sun.tools.javac.util.Assert;

import com.zxx.tinycat.core.http.exception.ErrorEnum;
import com.zxx.tinycat.core.http.exception.HttpParseException;

public class HttpRequestBodyParser {
    public static byte[] parse(HttpRequestReader reader, int contentLength) {
        if (reader.payload.length() - reader.pos < contentLength) {
            //reader的长度不够解析请求体
            throw new HttpParseException(ErrorEnum.HTTP_REQUEST_PARSE_UNFINISHED);
        }
        byte[] responseByte = reader.payload().substring(reader.pos(), reader.pos + contentLength).getBytes();
        reader.consumePos(contentLength);

        return responseByte;
    }

}
