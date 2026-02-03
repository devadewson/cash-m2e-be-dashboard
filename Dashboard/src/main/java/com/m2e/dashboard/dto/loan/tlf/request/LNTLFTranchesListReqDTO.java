package com.m2e.dashboard.dto.loan.tlf.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;

@Data
public class LNTLFTranchesListReqDTO extends BasePageRequest {
    private String accountCurrency;
    private String acctNumber;
    private String searchParam;
}