package com.m2e.dashboard.dto.task.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDTO implements Serializable {
    private String acctNumber;
    private String acctName;
    private String taskId;
    private String taskType;
    private String taskAction;
    private String taskStatus;
    private String canApprove;
    private List<String> moreActions;
}
