package com.m2e.dashboard.sp.loan.tlf.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class LNTLFTrxHistorySpDTO implements Serializable {
    private String date; // format : dd MMM yyyy HH:mm
    private String trxReference;
    private String trxDesc;
    private String debit;
    private String credit;
}
