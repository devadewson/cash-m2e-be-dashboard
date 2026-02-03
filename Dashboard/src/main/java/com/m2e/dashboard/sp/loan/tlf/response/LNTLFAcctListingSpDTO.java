package com.m2e.dashboard.sp.loan.tlf.response;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;

@Data
public class LNTLFAcctListingSpDTO extends BasePageRequest {
    private String acctName;
    private String acctNumber;
    private String accountCurrency;
    private String drawdownAmount;
    private String outstandingBalance;
    private String isForeign;
    private String isIslamic;
    private String isFitas;
    private String provider;
    private String isOverdue;
    private String hasSellingPrice;
    private String productCode;
    private String acctStatusCode;
    private String acctStatusDesc;
    private String loanGrading;
}