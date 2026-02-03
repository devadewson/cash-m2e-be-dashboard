package com.m2e.dashboard.dto.loan.rc.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class LNRevolvingCreditDetailSGDTO extends LNRevolvingCreditDetailDTO {
    private String repaymentAmount;
    private String outstandingLoanAmount;
    private String interestAmount;
    private String productName;
    private String lateInterestAmount;
    private String originalLoanAmount;
    private String interestRate;
    private String nextInterestRepaymentDate; // format: dd MMM yyyy
    private String nextInterestRefixDate; // format: dd MMM yyyy
    @JsonProperty("isFITAS")
    private boolean isFitas = false;
}
