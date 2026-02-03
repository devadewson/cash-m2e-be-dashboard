package com.m2e.dashboard.sp.otherbank.request;

import lombok.Data;

@Data
public class OtherBankTrxHistListSpParam {
    private String acctNumber;
    private String trxCode;
    private String searchParam;
    private String startDateTime;
    private String endDateTime;
    private Double amtFrom;
    private Double amtTo;
    private String userId;
    private String corpId;
    private String msgType;
}
