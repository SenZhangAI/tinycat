package com.zxx.tinycat.core.http.exception;

public enum ErrorEnum {
    HTTP_REQUEST_PARSE_FAIL(1, "Http请求报文解析失败"),
    HTTP_REQUEST_PARSE_UNFINISHED(2, "Http请求报文解析未结束"),
    ;
    final private int code;
    final private String msg;

    ErrorEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

}
