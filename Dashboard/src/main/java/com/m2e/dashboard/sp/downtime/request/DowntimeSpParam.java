package com.m2e.dashboard.sp.downtime.request;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class DowntimeSpParam {
    private String userId;
    private OffsetDateTime timeNow;
}
