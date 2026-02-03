package com.m2e.dashboard.sp.task.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskInsertSpParam implements Serializable {
    private String id;
    private String createdBy;
    private String updatedBy;
    private OffsetDateTime dtCreated;
    private Timestamp tsCreated;
    private OffsetDateTime dtUpdated;
    private Timestamp tsUpdated;
    private String sysPvdCd;
    private String mtApprSts;
    private String apprBy;
    private Integer version;
    private String cfoTaskId;
    private String refNo;
    private String cfoFuncsId;
    private String cfoFuncsActionId;
    private String remark;

}

