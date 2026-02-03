package com.m2e.dashboard.dto.task.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskListingDTO implements Serializable {
    private String corpCifNo;
    private SearchCollectionResponse<TaskDTO> datas;
}
