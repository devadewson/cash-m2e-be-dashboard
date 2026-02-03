package com.m2e.dashboard.dto.task.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskDetailReqDTO implements Serializable {
    private String taskId;
}
