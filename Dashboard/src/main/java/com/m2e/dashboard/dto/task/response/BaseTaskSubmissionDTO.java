package com.m2e.dashboard.dto.task.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseTaskSubmissionDTO implements Serializable {
    private String action;
    private String dateCreated; // same value with requestedDate
    private String productType;
    private String approvalStatus;
    private String requestedBy;
    private String requestedDate;
}
