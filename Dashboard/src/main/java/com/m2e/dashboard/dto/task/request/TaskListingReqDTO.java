package com.m2e.dashboard.dto.task.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.enums.SortByEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
public class TaskListingReqDTO extends BasePageRequest {
    private String corpCifNo;
    private String searchParam;
    private TaskFilterReqDTO filter;
    private SortByEnum sortBy;
}
