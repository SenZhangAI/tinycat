package com.zxx.tinycat.core.http.request;


public enum RequestMethodEnum {
    //Http请求方式
    PUT("PUT", "Put请求"),
    GET("GET", "Get请求"),
    DELETE("DELETE", "Delete请求"),
    POST("POST", "Post请求"),
    OPTION("OPTION", "Option请求"),
    ;

    private final String code;
    private final String text;

    RequestMethodEnum(String code, String text) {
        this.code = code;
        this.text = text;
    }

    public String getCode() {
        return code;
    }
    public String getText() {
        return text;
    }


    RequestMethodEnum fromCode() throws Exception {
        for (RequestMethodEnum item : values()) {
            if (item.getCode().equals(code)) {
                return item;
            }
        }
        throw new Exception("未知请求方式");
    }

}
