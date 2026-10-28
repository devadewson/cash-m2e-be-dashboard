package com.m2e.dashboard.sp.deposit.ca.response;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;

import java.io.Serializable;

@Data
public class DepositAcctSummarySpDTO implements Serializable {
    private String acctName;
    private String acctNumber;
    private String currency;
    private String ledgerBalance;
    private String availableBalance;
    private String provider;
    private String acctType;
    private String isOD;
    private String isIslamic;
    private MoreActionsEntitlement moreActions;
    private String acctStatusCode;
    private String acctStatusDesc;

    // Khusus Fixed Deposit, null untuk CA dan SA
    private String principal;
    private Integer tenor;
    private String interestRate;
    private String placementDate;
    private String maturityDate;
}
