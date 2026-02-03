package com.m2e.dashboard.dto.loan.rc.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class LNRevolvingCreditTrxHistoryDTO implements Serializable {
    private String date; // format : dd MMM yyyy HH:mm
    private String trxReference;
    private String trxDesc;
    private String credit;
    private String debit;
    private String currency;
}
