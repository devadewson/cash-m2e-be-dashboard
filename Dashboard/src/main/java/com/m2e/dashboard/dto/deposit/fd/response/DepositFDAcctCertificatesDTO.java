package com.m2e.dashboard.dto.deposit.fd.response;

import lombok.Data;

@Data
public class DepositFDAcctCertificatesDTO {
    private String certificateNumber;
    private String preferredName;
    private String profitRate;
    private String maturityDate;
    private String principalAmount;
    private String currency;
}
