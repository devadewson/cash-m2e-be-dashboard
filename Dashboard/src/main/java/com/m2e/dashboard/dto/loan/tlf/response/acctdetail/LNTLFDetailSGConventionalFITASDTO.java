package com.m2e.dashboard.dto.loan.tlf.response.acctdetail;

import com.m2e.dashboard.dto.loan.tlf.response.LNTLFDetailDTO;
import com.m2e.dashboard.enums.LNProductType;
import com.m2e.utils.esb.enums.loan.tlf.FitasLoanGrading;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LNTLFDetailSGConventionalFITASDTO extends LNTLFDetailDTO {
    private LNProductType productType;
    private String acctStatusCode;
    private String acctStatusDesc;
    private String term;
    private String installmentAmount;
    private String overdueInstallmentAmount;
    private String interestRate;
    private String originalLoanAmount;
    private String repaymentAmount;
    private String lateInterestAmount;
    private String nextInterestPaymentDate;
    private String nextPrincipalRepaymentDate;
    private FitasLoanGrading loanGrading;
}