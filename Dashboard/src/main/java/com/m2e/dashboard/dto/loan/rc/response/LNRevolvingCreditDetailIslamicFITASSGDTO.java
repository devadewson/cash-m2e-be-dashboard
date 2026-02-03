package com.m2e.dashboard.dto.loan.rc.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class LNRevolvingCreditDetailIslamicFITASSGDTO extends LNRevolvingCreditDetailDTO {
    private String repaymentAmount;
    private String outstandingFinancingAmount;
    private String profitAmount;
    private String productName;
    private String profitRate;
    private String originalFinancingAmount; // format: dd MMM yyyy
    private String latePaymentCharges; // format: dd MMM yyyy
    boolean isIslamic;
    boolean isFITAS;
}