package com.m2e.dashboard.dto.deposit.va.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor

public class DepositVASearchDto {
    private String corpCode;
    private String virtualAcctName;
    private String virtualAcctNumber;
    private String virtualAcctRefNumber;

}
