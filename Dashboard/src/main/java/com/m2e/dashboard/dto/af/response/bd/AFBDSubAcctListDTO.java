package com.m2e.dashboard.dto.af.response.bd;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;

import java.io.Serializable;

@Data
public class AFBDSubAcctListDTO implements Serializable {
    private String financedAmt;
    private String outstandingBalance;
    private String currency;
    private String provider;
    private MoreActionsEntitlement moreActions;
}
