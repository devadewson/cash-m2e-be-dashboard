package com.m2e.dashboard.dto.base;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class OpenTagCorpPageDTO {
    private Integer totalPages;
    private Integer currentPage;
    private Integer nextPage;
    private Boolean isSubscriberCompOnly; //field for FE to check whether to display subscriber comp view only
    private SearchCollectionResponse<OpenTagComp> data;
}
