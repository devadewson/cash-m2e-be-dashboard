package com.m2e.dashboard.dto.af.response.fs;

import lombok.Data;

@Data
public class AFFSAcctDetailDTO {
    private String acctName;
    private String currency;
    private String lastUpdatedDateTime;
    private String outstandingBalance;
    private String acctStatusCode;
    private String acctStatusDesc;
    private boolean isIslamic;
}