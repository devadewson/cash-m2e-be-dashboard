package com.m2e.dashboard.dto.task.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskListStatsDTO {

    private String acctNumber;
    private String  acctName;
    private String taskId;
    private String taskType;
    private String taskAction;
    private String taskStatus;
    private String dtCreated;

}
