package com.m2e.dashboard.dto.af.response.bd;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFBDSubAcctListSGDTO implements Serializable {
    private String blockNumber;
    private String collateralId;
    private String financedAmt;
    private String outstandingBalance;
    private String instalmentAmt;
    private String settlementAmount;
    private String instalmentAmtOverdue;
    private String currency;
    private String provider;
    private String acctStatusDesc;
    private String tlastIndicator;
    private MoreActionsEntitlement moreActions;
}
