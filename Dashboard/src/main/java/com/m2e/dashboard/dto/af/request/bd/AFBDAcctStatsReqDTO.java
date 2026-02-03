package com.m2e.dashboard.dto.af.request.bd;

import com.m2e.dashboard.enums.FilterAutoFinanceAccountListingEnum;
import lombok.Data;

@Data
public class AFBDAcctStatsReqDTO {
    FilterAutoFinanceAccountListingEnum viewByIndicator;
    Boolean realTimeIndicator;
}
