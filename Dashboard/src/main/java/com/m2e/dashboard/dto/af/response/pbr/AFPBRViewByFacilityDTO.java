package com.m2e.dashboard.dto.af.response.pbr;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFPBRViewByFacilityDTO implements Serializable {
    private String masterAcctName;
    private String masterAcctNumber;
    private String accountCurrency;
    private String noOfAccount;
    private String totalFacilityLimit;
    private String provider;
    private String totalSettlementAmount;
    private String acctStatusCode;
    private String acctStatusDesc;
    private MoreActionsEntitlement moreActions;

}
