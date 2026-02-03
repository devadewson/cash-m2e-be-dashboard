package com.m2e.dashboard.dto.task.request;

import com.m2e.dashboard.enums.ApprovalStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskApprovalReqDTO implements Serializable {
    private String taskId;
    private ApprovalStatusEnum approvalStatus;
    private String rejectionNote;
}
