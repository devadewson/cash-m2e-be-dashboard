package com.m2e.dashboard.dto.af.request.fs;

import com.m2e.dashboard.enums.FilterAutoFinanceAccountListingEnum;
import lombok.Data;

@Data
public class AFFSAcctStatsReqDTO {
    private boolean realTimeIndicator;
    private FilterAutoFinanceAccountListingEnum viewByIndicator;
}
