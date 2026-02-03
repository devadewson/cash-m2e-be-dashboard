package com.m2e.dashboard.dto.af.response.bd;

import lombok.Data;

import java.io.Serializable;

@Data
public class AFBDTrxHistoryDTO implements Serializable {
    private String trxDate;
    private String trxReference;
    private String trxDesc;
    private String debit;
    private String credit;

    private String processingTime;
    private String trxDesc2;
    private String trxDesc3;
    private String trxDesc4;
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
