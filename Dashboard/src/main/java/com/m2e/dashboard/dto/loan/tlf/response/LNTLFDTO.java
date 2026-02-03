package com.m2e.dashboard.dto.loan.tlf.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.dto.MoreActionsEntitlement;
import com.m2e.dashboard.dto.loan.tlf.LNTLFAccountType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LNTLFDTO {
    private LNTLFAccountType accountTypeIndicators;
    private String acctName;
    private String acctNumber;
    private String accountCurrency;
    private String productCode;
    private String acctStatusCode;
    private String acctStatusDesc;
    private String drawdownAmount;
    private String outstandingBalance;
    private String provider;
    private String loanGrading;
    @JsonProperty("isOverdue")
    private boolean isOverdue;
    private boolean hasSellingPrice;
    private MoreActionsEntitlement moreActions;
}
