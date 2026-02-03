package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum SortByEnum {
    ASC("ASC"),
    DESC("DESC");

    private final String query;

    SortByEnum(String query){
        this.query = query;
    }
}
