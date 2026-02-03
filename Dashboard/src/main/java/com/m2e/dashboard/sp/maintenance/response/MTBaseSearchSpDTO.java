package com.m2e.dashboard.sp.maintenance.response;

import lombok.Data;

import java.io.Serializable;

@Data
public class MTBaseSearchSpDTO implements Serializable {
    private String acctName;
    private String acctNumber;
    private String defaultName;
    private String product;
    private String subProduct;
    private String currency;
    private String resetToDefault;
    private String isPendingApproval;
}
