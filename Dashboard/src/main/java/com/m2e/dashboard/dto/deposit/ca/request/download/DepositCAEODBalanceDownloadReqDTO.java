package com.m2e.dashboard.dto.deposit.ca.request.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DepositCAEODBalanceDownloadReqDTO extends DownloadReq {
    private String acctNumber;
    private String currency;
    private int year;
    private int month;
}