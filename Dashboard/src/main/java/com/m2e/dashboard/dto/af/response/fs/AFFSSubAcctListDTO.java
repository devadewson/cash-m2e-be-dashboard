package com.m2e.dashboard.dto.af.response.fs;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFFSSubAcctListDTO implements Serializable {
    private String masterAcctName;
    private String certNo;
    private String currency;
    private String financedAmount;
    private String outstandingBalance;
    private String settlementAmt;
    private MoreActionsEntitlement moreActions;
    private String acctStatusCode;
    private String acctStatusDesc;
}