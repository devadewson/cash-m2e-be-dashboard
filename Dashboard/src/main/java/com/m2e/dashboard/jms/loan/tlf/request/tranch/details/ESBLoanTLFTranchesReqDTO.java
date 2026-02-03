package com.m2e.dashboard.jms.loan.tlf.request.tranch.details;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ESBLoanTLFTranchesReqDTO implements Serializable {
    private String acctNumber;
    private String accountCurrency;
    private String searchParam;
    private String mbbLastKey;
    private int page;
    private int pageSize;
}
