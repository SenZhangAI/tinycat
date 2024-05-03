package com.zxx.tinycat.core.http.request.parser;

//import com.sun.tools.javac.util.Assert;

import java.lang.String;
import com.zxx.tinycat.core.RequestMethodEnum;
import com.zxx.tinycat.core.http.exception.ErrorEnum;
import com.zxx.tinycat.core.http.exception.HttpParseException;

public class HttpRequestReader {
    final StringBuffer payload;
    int pos;
    int line;
    int column;
    private final MyStringBuffer buffer = new MyStringBuffer();
    private Boolean fail;


    public HttpRequestReader(String payload) {
        this.payload = new StringBuffer(payload);
        this.line = 1;
        this.pos = 0;
        this.column = 1;
    }

    public Boolean getFail() {
        return fail;
    }

    public void freshPos() {
        this.line = 1;
        this.pos = 0;
        this.column = 1;
    }

    public void setFail(Boolean fail) {
        this.fail = fail;
    }

    public int pos() {
        return this.pos;
    }

    public int line() {
        return this.line;
    }

    public int column() {
        return this.column;
    }

    public String payload() {
        return this.payload.toString();
    }
    public void appendPayLoad(String payload) {
        this.payload.append(payload);
    }

    public String remainStr() {
        return payload.toString().substring(pos);
    }

    public char peek() {
        if (pos >= payload.length()) {
            throw new HttpParseException(ErrorEnum.HTTP_REQUEST_PARSE_UNFINISHED);
        }
        return payload.charAt(pos);
    }

    public int peekNextChar() {
        if (pos + 1 >= payload.length()) {
            throw new HttpParseException(ErrorEnum.HTTP_REQUEST_PARSE_UNFINISHED);
        }
        return payload.charAt(pos + 1);
    }

    private void nextColumn() {
        this.pos++;
        this.column++;
    }

    private void nextLine() {
        this.column = 1;
        this.line++;
        this.pos++;
    }

    public boolean isEmpty() {
        return pos >= payload.length();
    }

    public boolean isNotEmpty() {
        return pos < payload.length();
    }


    public void mustConsumeSpace() {
        if (isEmpty()) {
            throw new HttpParseException(ErrorEnum.HTTP_REQUEST_PARSE_UNFINISHED);
        }
        if (peek() != ' ') {
            throw new HttpParseException(ErrorEnum.HTTP_REQUEST_PARSE_FAIL, "消费空格异常");
        }
        nextColumn();
    }

    public void consumePos(int position) {
        for (int i = 0; i < position; i++) {
            switch (peek()) {
                case '\n':
                    nextLine();
                    break;
                default:
                    nextColumn();
                    break;
            }
        }
    }


    public String consumeToEndChar(char endChar) {
        buffer.clear();

        while (peek() != endChar) {
            if (isEmpty()) {
                throw new HttpParseException(ErrorEnum.HTTP_REQUEST_PARSE_UNFINISHED);
            }
            buffer.append(peek());
            nextColumn();
        }

        //消费endChar
        nextColumn();
        if (buffer.toString().isEmpty()) {
            throw new HttpParseException(ErrorEnum.HTTP_REQUEST_PARSE_UNFINISHED, "解析String异常，长度为空");
        }
        return buffer.toString();
    }

    public String consumeToLineEnd() {
        buffer.clear();

        while (!(isEmpty() || (peek() == '\n' || (peek() == '\r' && peekNextChar() == '\n')))) {
            buffer.append(peek());
            nextColumn();
        }
        if (isNotEmpty()) {
            consumeNewLine();
        }
        return buffer.toString();
    }

    public void consumeNewLine() {
        if (peek() == '\n') {
            nextLine();
        } else if (peek() == '\r' && peekNextChar() == '\n') {
            nextColumn();
            nextLine();
        } else {
            throw new HttpParseException(ErrorEnum.HTTP_REQUEST_PARSE_FAIL, "消费换行异常, peek=" + peek() + ", peekNextChar=" + peekNextChar());
        }
    }

    private RequestMethodEnum consumeRequestMethod() {
        consumeWhiteSpaces();
        String requestMethod = consumeToEndChar(' ');
        return RequestMethodEnum.valueOf(requestMethod);
    }

    private String consumeRequestUrl() {
        return consumeToEndChar(' ');
    }

    private String consumeHttpVersion() {
        return consumeToLineEnd();
    }





    public void consumeWhiteSpaces() {
        while (isNotEmpty()) {
            switch (peek()) {
                case ' ':
                case '\t':
                    nextColumn();
                    break;
                case '\r':
                    if (peekNextChar() == '\n') {
                        nextColumn();
                    }
                    nextLine();
                    break;
                case '\n':
                    nextLine();
                    break;
                default:
                    return;
            }
        }
    }


    class MyStringBuffer {
        private final char[] charBuffer = new char[1024];
        private int pos = 0;
        private final StringBuffer buffer = new StringBuffer();

        public void append(char i) {
            charBuffer[pos++] = i;
            if (pos == 1024) {
                buffer.append(charBuffer, 0, pos);
                pos = 0;
            }
        }

        @Override
        public String toString() {
            if (pos > 0) {
                buffer.append(charBuffer, 0, pos);
                pos = 0;
            }
            return buffer.toString();
        }

        public void clear() {
            pos = 0;
            buffer.setLength(0);
        }
    }

}
