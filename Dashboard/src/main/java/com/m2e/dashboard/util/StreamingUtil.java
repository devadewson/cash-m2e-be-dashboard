package com.m2e.dashboard.util;

import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

public class StreamingUtil {
    private StreamingUtil() {
        // Prevent instantiation
    }

    public static StreamingResponseBody createStreamingResponse(byte[] fileContent) {
        return outputStream -> {
            try (InputStream inputStream = new ByteArrayInputStream(fileContent)) {
                byte[] buffer = new byte[8192]; // 8KB buffer size
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
                throw new RuntimeException("Error streaming the file content", e);
            }
        };
    }
}
