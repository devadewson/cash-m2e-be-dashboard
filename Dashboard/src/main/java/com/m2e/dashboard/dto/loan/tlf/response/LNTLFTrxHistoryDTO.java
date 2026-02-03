package com.m2e.dashboard.dto.loan.tlf.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class LNTLFTrxHistoryDTO implements Serializable {
    private String date; // format : dd MMM yyyy HH:mm
    private String trxReference;
    private String trxDesc;
    private String debit;
    private String credit;
    private String currency;
}
