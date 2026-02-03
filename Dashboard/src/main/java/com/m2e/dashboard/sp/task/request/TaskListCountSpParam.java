package com.m2e.dashboard.sp.task.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TaskListCountSpParam {
    private String userId;
    private String corpId;
}
