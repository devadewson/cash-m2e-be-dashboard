package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum GraphEnum {
    //ONE WEEK
    W("1W"),
    //ONE MONTH
    M("1M"),
    //ONE YEAR
    Y("1Y"),
    //ONE QUARTER
    Q("1Q"),
    ;
    private final String value;
    GraphEnum(String value) { this.value = value; }
}
