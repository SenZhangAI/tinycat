package com.zxx.tinycat.core.http.handler;


import com.zxx.tinycat.core.http.file.MimeTypes;
import com.zxx.tinycat.core.http.request.HttpRequest;
import com.zxx.tinycat.core.http.response.HttpResponse;
import com.zxx.tinycat.core.http.response.HttpResponseStatusCode;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HttpStaticFileHandler implements HttpGeneralHandlerInterface {
    final String rootDir;

    public HttpStaticFileHandler(final String rootDir) {
        this.rootDir = rootDir;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        Path rootPath = Paths.get(rootDir).toAbsolutePath().normalize();
        Path filePath = Paths.get(rootDir, request.getUrl()).normalize().toAbsolutePath();

        // Check if the requested file is within the allowed directory
        if (!filePath.startsWith(rootPath)) {
            return new HttpResponse(HttpResponseStatusCode.FORBIDDEN);
        }

        File file = filePath.toFile();
        if (!file.exists()) {
            return new HttpResponse(HttpResponseStatusCode.NOT_FOUND);
        }
        if (file.isDirectory()) {
            // Default to index.html if directory
            file = new File(file, "index.html");
            if (!file.exists()) {
                return new HttpResponse(HttpResponseStatusCode.NOT_FOUND);
            }
        }

        HttpResponse response = new HttpResponse();
        try {
            //TODO 优化 不能直接拿file.length做Byte的大小
            String contentType = getContentType(file);
            FileInputStream fileInputStream = new FileInputStream(file);
            byte[] fileContent = new byte[(int) file.length()];
            fileInputStream.read(fileContent);
            fileInputStream.close();

            response.setCode(HttpResponseStatusCode.OK);
            response.addHeader("Content-Type", contentType);
            response.setBody(fileContent);
            response.addHeader("Content-Length", String.valueOf(fileContent.length));
        } catch (IOException e) {
            return new HttpResponse(HttpResponseStatusCode.INTERNAL_SERVER_ERROR);
        }

        return response;
    }

    private static String getContentType(File file) {
        String name = file.getName();
        int lastDot = name.lastIndexOf('.');
        if (lastDot != -1) {
            String extension = name.substring(lastDot);
            return MimeTypes.getContentType(extension);
        }
        // Default MIME type
        return "application/octet-stream";
    }

}
