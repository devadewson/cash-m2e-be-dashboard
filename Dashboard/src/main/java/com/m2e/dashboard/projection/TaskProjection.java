package com.m2e.dashboard.projection;

import java.sql.Timestamp;

public interface TaskProjection {
    String getId();
    String getTaskId();
    String getRefNumber();
    String getApprSts();
    String getFunction();
    String getFunctionAction();
    String getCreatedBy();
    String getCreatedDate();
}
