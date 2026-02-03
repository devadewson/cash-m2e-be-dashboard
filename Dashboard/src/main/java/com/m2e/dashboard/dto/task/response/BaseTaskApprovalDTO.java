package com.m2e.dashboard.dto.task.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BaseTaskApprovalDTO<T> implements Serializable {
    private String approvedBy;
    private String approvedDate;
    private String rejectedBy;
    private String rejectedDate;
    private String rejectionNotes;
    private T submissionData;
}
