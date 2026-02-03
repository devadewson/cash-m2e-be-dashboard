package com.m2e.dashboard.sp.otherbank.request;

import lombok.Data;

@Data
public class OtherBankAcctListSpParam {
    private String searchParam;
    private String userId;
    private String corpId;
    private String corpCifNo;
    private String product;
}
