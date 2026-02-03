package com.m2e.dashboard.dto.af.response.bd;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFBDAcctListDTO implements Serializable {
    protected String masterAcctNumber;
    protected String currency;
    protected String provider;
    protected String acctStatusCode;
    protected String acctStatusDesc;
    protected MoreActionsEntitlement moreActions;
}
