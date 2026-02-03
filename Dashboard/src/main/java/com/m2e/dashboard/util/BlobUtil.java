package com.m2e.dashboard.util;

import com.m2e.dashboard.controller.CustomizeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Blob;

public class BlobUtil {

    private static final Logger logger = LoggerFactory.getLogger(BlobUtil.class);

    public static String convertBlobToString(Blob blob) {
        if (blob == null) {
            return null;
        }

        try (InputStream inputStream = blob.getBinaryStream()) {
            byte[] byteArray = inputStream.readAllBytes();
            return new String(byteArray, StandardCharsets.UTF_8)
                    .replace("\u0001", "")
                    .replace("\u0003", "");
        } catch (Exception e) {
            logger.error("Error while converting Blob to String: {}", e.getMessage());
            return null;
        }
    }
}
