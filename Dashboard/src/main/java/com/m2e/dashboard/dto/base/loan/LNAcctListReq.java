package com.m2e.dashboard.dto.base.loan;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
public class LNAcctListReq extends BasePageRequest {
    private boolean realTimeIndicator;
    private String searchParam;
    private String corpCifNo;
}