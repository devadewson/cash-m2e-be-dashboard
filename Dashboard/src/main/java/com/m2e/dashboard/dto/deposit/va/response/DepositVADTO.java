package com.m2e.dashboard.dto.deposit.va.response;

import lombok.Data;


@Data
public class DepositVADTO {

    private String virtualAcctName;
    private String virtualAcctNumber;
    private String virtualAcctCurrency;

    private String currentAcctName;
    private String currentAcctNumber;
    private String currentAcctId;

    private String currentAcctCifNo;

    private String corpCode;

    private String virtualAcctRefNumber;
    private String virtualAcctId;
    private String systemProviderCode;

    private String dateCreated;
    private String dateUpdated;
    private String dateClosed;

    private String dateCreatedInt;
}
