package com.zxx.tinycat.core.http.file;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MimeTypesTest {
    @Test
    void getMimeTypes() {
        String contentType = MimeTypes.getContentType(".html");
        System.out.println(contentType);
    }

}