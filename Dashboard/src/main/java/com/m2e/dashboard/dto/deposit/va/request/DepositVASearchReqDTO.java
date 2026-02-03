package com.m2e.dashboard.dto.deposit.va.request;

import lombok.Data;


@Data
public class DepositVASearchReqDTO {
    private String currAcctNumber;
    private String virtualAcctNumber;
    private String corporateCode;
    private String searchParam;
    private int page;
    private int pageSize;
}
