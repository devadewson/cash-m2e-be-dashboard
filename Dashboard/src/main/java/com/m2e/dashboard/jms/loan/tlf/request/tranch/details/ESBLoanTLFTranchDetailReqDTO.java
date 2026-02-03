package com.m2e.dashboard.jms.loan.tlf.request.tranch.details;

import com.m2e.dashboard.enums.LNProductType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ESBLoanTLFTranchDetailReqDTO {
    private String acctNumber;
    private String trancheNumber;
    private String currency;
    private LNProductType productType;
    private String mbbLastKey;
}
