package com.m2e.dashboard.projection;

import jakarta.persistence.Lob;

import java.sql.Blob;

public interface DepositCASwiftMT103Projection {
    @Lob
    Blob getFileContent();
    String getFileType();
}
