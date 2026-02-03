package com.m2e.dashboard.dto.af.response.pbr;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFPBRUnitSummaryDTO implements Serializable {
    private String acctName;
    private String unitNumber;
    private String accountCurrency;
    private String financedAmount;
    private String settlementAmount;
    private String amountDue;
    private String outstandingBalance;
    private String acctStatusDesc;

    private MoreActionsEntitlement moreActions;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String masterAcctNumber;
}
