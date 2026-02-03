package com.m2e.dashboard.dto.deposit.ca.response;

import lombok.Data;

@Data
public class DepositGPIStatusDTO {
    private String uetrNumber;
    private String gpiStatus;
    private String gpiReason;
    private String totalElapsedTime;
    private DepositOriginatingBankDTO originatingBank;
    private DepositIntermediaryBank1DTO intermediaryBank1;
    private DepositIntermediaryBank2DTO intermediaryBank2;
    private DepositRecipientBankDTO recipientBank;

}
