package com.m2e.dashboard.dto.deposit.ca.response;

import lombok.Data;

@Data
public class DepositIntermediaryBank2DTO {
    private String intermediaryBank;
    private String acceptanceDateTime;
    private String releaseDateTime;
    private String elapsedTime;
}
