package com.m2e.dashboard.service;

public interface FileService<T> {
    byte[] generateCsv(T datas);
    byte[] generatePdf(T datas);
    byte[] generateTxt(T datas);
}
