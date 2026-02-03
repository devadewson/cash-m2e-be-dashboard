package com.m2e.dashboard.dto.base;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class DownloadDTO implements Serializable {
    private byte[] data;
    private String fileName;
}
