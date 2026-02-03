package com.m2e.dashboard.dto.task.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TaskFilterReqDTO implements Serializable {
    private List<String> type;
    private List<String> status;
}
