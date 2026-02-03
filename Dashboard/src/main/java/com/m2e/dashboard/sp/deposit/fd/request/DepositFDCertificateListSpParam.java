package com.m2e.dashboard.sp.deposit.fd.request;

import lombok.Data;

@Data
public class DepositFDCertificateListSpParam {
    private String acctNumber;
    private String searchParam;
    private String userId;
    private String corpId;
}
