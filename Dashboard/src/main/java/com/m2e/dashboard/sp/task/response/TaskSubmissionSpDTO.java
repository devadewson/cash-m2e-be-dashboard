package com.m2e.dashboard.sp.task.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskSubmissionSpDTO implements Serializable {
    private String id;
    private String taskId;
}
