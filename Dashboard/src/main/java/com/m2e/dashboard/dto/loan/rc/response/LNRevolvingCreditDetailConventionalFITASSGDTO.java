package com.m2e.dashboard.dto.loan.rc.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class LNRevolvingCreditDetailConventionalFITASSGDTO extends LNRevolvingCreditDetailDTO {
    private String repaymentAmount;
    private String outstandingLoanAmount;
    private String interestAmount;
    private String productName;
    private String lateInterestAmount;
    private String originalLoanAmount;
    private String interestRate;
    private String nextInterestRepaymentDate; // format: dd MMM yyyy
    private String nextInterestRefixDate; // format: dd MMM yyyy
    boolean isIslamic;
    boolean isFITAS;
}
