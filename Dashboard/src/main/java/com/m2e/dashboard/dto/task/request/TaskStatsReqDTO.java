package com.m2e.dashboard.dto.task.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.SortByEnum;
import lombok.Data;

@Data
public class TaskStatsReqDTO {
    private String corpCifNo;
    private String searchParam;
}
