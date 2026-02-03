package com.m2e.dashboard.dto.base;

import com.m2e.dashboard.enums.FilterAutoFinanceAccountListingEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class SearchResultCountAFReqDTO extends SearchResultCountReqDTO {
    private FilterAutoFinanceAccountListingEnum viewByIndicator;
}
