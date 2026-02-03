package com.m2e.dashboard.sp.task.request;

import lombok.Data;

import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Data
public class TaskHistoryInsertSpParam {
    private String id;
    private String createdBy;
    private String updatedBy;
    private OffsetDateTime dtCreated;
    private Timestamp tsCreated;
    private OffsetDateTime dtUpdated;
    private Timestamp tsUpdated;
    private Integer version;
    private String cfoTaskId;
    private String mtApprSts;
    private String remark;
}
