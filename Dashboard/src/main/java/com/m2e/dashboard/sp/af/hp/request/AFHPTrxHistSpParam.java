package com.m2e.dashboard.sp.af.hp.request;

import lombok.Data;

@Data
public class AFHPTrxHistSpParam {
    private String acctNumber;
    private String searchParam;
    private String trxCode;
    private String startDateTime;
    private String endDateTime;
    private String amtFrom;
    private String amtTo;
    private String userId;
    private String corpId;
}
