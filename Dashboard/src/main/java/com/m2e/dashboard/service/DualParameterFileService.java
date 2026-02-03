package com.m2e.dashboard.service;

public interface DualParameterFileService<T> {
    byte[] generateCsv(T datas, String[] headers);
    byte[] generatePdf(T datas, String[] headers);
    byte[] generateTxt(T datas, String[] headers);
}
