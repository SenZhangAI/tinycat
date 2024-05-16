package com.zxx.tinycat.core.http.file;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

@Slf4j
public class MimeTypes {
    private static final Map<String, String> mimeTypes = new HashMap<>();

    static {
        try {
            loadMimeTypes("/mime.types");
        } catch (IOException e) {
            log.error("load mime types error", e);
        }
    }

    private static void loadMimeTypes(String filePath) throws IOException {
        InputStream inputStream = MimeTypes.class.getResourceAsStream(filePath);
        if (inputStream == null) {
            log.error("Resource not found: " + filePath);
            return;
        }
        try (Scanner scanner = new Scanner(inputStream)) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                if (!line.startsWith("#") && line.contains(" ")) {
                    int spaceIndex = line.indexOf(' ');
                    String extension = line.substring(0, spaceIndex);
                    String type = line.substring(spaceIndex + 1).trim();
                    mimeTypes.put(extension, type);
                }
            }
        }
    }

    public static String getContentType(String extension) {
        return mimeTypes.getOrDefault(extension, "application/octet-stream");
    }
}
