package com.m2e.dashboard.sp.operationhour.request;

import lombok.Data;

import java.time.OffsetDateTime;

@Data
public class OperationHourSpParam {
    private String userId;
    private OffsetDateTime timeNow;
}
