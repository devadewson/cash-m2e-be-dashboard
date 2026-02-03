package com.m2e.dashboard.dto.loan.tlf.request;

import com.m2e.dashboard.dto.loan.tlf.LNTLFAccountType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
public class LNTLFDetailReqDTO implements Serializable {
    private LNTLFAccountType accountTypeIndicators;
    private String productCode;
    private String acctNumber;
    private String currency;
    private String corpCifNo;
    private String provider;
}