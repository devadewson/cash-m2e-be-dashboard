package com.m2e.dashboard.dto.af.request.pbr;

import com.m2e.dashboard.enums.FilterAutoFinanceAccountListingEnum;
import lombok.Data;

@Data
public class AFPBRAcctStatsReqDTO {
    FilterAutoFinanceAccountListingEnum viewByIndicator;
    Boolean realTimeIndicator;
}
