package com.m2e.dashboard.dto.af.response.hp;

import lombok.Data;

@Data
public class AFHPTrxHistDTO {

    private String trxDate;
    private String trxReference;
    private String trxDescription;
    private String debit;
    private String credit;

//    Customize field
    private String processingTime;
    private String trxDescription2;
    private String trxDescription3;
    private String trxDescription4;
    private String virtualAcct;
    private String trxStatus;
    private String trxType;

    private String srcCode;
    private String tellerId;
    private String ownerInstitution;
    private String trxCode;
    private String auxiliaryTrxCode;
    private String expiryDate;

    private String currency;

}
