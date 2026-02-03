package com.m2e.dashboard.dto.task.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskApprovalDTO <T> implements Serializable {
    private T data;
}
