package com.m2e.dashboard.sp.af.fs.response;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFFSAcctListSpDTO implements Serializable {
    private String masterAcctNumber;
    private String masterAcctName;
    private String currency;
    private String numberOfCert;
    private String totalFacilityLimit;
    private String provider;
    private String tlastIndicator;
    private String acctStatusCode;
    private String acctStatusDesc;
    private MoreActionsEntitlement moreActions;
}