package com.m2e.dashboard.enums;

import lombok.Getter;

@SuppressWarnings("java:S115")
@Getter
public enum DateTimeFormatPatternEnum {
    yyyyMMdd("yyyyMMdd"),
	yyyyMMddHHmmss("yyyyMMddHHmmss"),
    dd_MMM_yyyy("dd MMM yyyy"),
    dd_MMM_yyyy_DASH("dd MMM yyyy -"),
    dd_MMM_yyyy_00_00("dd MMM yyyy 00:00"),
    dd_MMM_yyyy_HH_mm("dd MMM yyyy HH:mm"),
    DEFAULT_TIME_00_00("00:00");

    private final String dateFormatPattern;
    DateTimeFormatPatternEnum(String dateFormatPattern) {
        this.dateFormatPattern = dateFormatPattern;
    }
}
