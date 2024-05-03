package com.zxx.tinycat.core.http.exception;

public class HttpParseException extends RuntimeException {
    private final int errorCode;
    private final String errorMessage;
    private String detail;

    public HttpParseException(ErrorEnum errorEnum) {
        this.errorCode = errorEnum.getCode();
        this.errorMessage = errorEnum.getMsg();
    }
    public HttpParseException(ErrorEnum errorEnum, String detail) {
        this.errorCode = errorEnum.getCode();
        this.errorMessage = errorEnum.getMsg();
        this.detail = detail;
    }

    @Override
    public String getMessage() {
        return errorCode + ":" + errorMessage + (detail == null ? "" : ":" + detail);
    }

    public int getErrorCode() {
        return errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getDetail() {
        return detail;
    }
}
