package com.m2e.dashboard.dto.base;

import lombok.Data;

@Data
public class OpenTagComp {
    private String corpId; // Hidden at FE & to be passed by FE during account listing.
    private String companyName;
    private String corpCifNo;
    private String provider;
    private Integer noOfAccounts;
    private Boolean isSubscriberCompOnly; //field for FE to check whether to display subscriber comp view only
}
