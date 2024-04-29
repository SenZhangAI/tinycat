package com.zxx.tinycat.core.http.request;

//import com.sun.tools.javac.util.Assert;

public class HttpRequestReader {
    final String payload;
    int pos;
    int line;
    int column;
    private final MyStringBuffer buffer = new MyStringBuffer();

    public HttpRequestReader(String payload) {
        this.payload = payload;
        this.line = 1;
        this.pos = 0;
        this.column = 1;
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

    public final String payload() {
        return this.payload;
    }

    public char peek() {
        return payload.charAt(pos);
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
//       Assert.check(peek() == ' ', "消费空格异常");
       nextColumn();
    }


    public String consumeStr(char endChar) throws Exception {
        buffer.clear();

        while (!isEmpty() && peek() != endChar) {
            buffer.append(peek());
            nextColumn();
        }
        //消费endChar
        if (endChar == '\n') {
            nextLine();
        } else {
            nextColumn();
        }
//        Assert.check(buffer.toString().length() > 0, "解析String异常，长度为空");
        return buffer.toString();
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
