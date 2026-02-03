package com.m2e.dashboard.util;

import com.m2e.dashboard.dto.base.DownloadDTO;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.Arrays;

public class DownloadUtil {

    public static HttpHeaders setHttpHeadersForDownload(DownloadDTO result) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + result.getFileName() + "\"");
        headers.setAccessControlExposeHeaders(new ArrayList<>(Arrays.asList(HttpHeaders.CONTENT_DISPOSITION)));
        headers.setContentLength(result.getData() == null ? 0 : result.getData().length);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        return headers;
    }
}
