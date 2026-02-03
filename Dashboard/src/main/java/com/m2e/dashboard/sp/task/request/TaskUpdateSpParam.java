package com.m2e.dashboard.sp.task.request;

import lombok.Data;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Data
public class TaskUpdateSpParam implements Serializable {
    private String cfoTaskId;
    private String mtApprSts;
    private String remark;
    private String updatedBy;
    private OffsetDateTime dtUpdated;
    private Timestamp tsUpdated;

}
