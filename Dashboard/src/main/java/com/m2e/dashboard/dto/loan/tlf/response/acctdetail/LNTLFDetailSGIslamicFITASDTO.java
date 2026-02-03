package com.m2e.dashboard.dto.loan.tlf.response.acctdetail;

import com.m2e.dashboard.dto.loan.tlf.response.LNTLFDetailDTO;
import com.m2e.dashboard.enums.LNProductType;
import com.m2e.utils.esb.enums.loan.tlf.FitasLoanGrading;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LNTLFDetailSGIslamicFITASDTO extends LNTLFDetailDTO {
    private LNProductType productType;
    private String acctStatusCode;
    private String acctStatusDesc;
    private String installmentAmount;
    private String installmentCurrency;
    private String overdueInstallmentAmount;
    private String profitRatePA;
    private String originalFinancingAmount;
    private String repaymentAmount;
    private String latePaymentChargesAmount;
    private String nextProfitRepaymentDate;
    private String nextPrincipalRepaymentDate;
    private FitasLoanGrading loanGrading;
}