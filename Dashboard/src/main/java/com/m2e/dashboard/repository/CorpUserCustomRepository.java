package com.m2e.dashboard.repository;

public interface CorpUserCustomRepository {
    int updateDemoScreenExplored(String userId, String Corp, String columnName);
}