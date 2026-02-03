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
public class TaskVarInsertSpParam implements Serializable {
    private String id;
    private String createdBy;
    private String updatedBy;
    private OffsetDateTime dtCreated;
    private Timestamp tsCreated;
    private OffsetDateTime dtUpdated;
    private Timestamp tsUpdated;
    private Integer version;
    private String cfoTaskId;
    private String varName;
    private String varValue;

}
