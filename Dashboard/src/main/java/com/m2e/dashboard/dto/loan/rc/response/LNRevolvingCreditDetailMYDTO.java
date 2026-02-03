package com.m2e.dashboard.dto.loan.rc.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@NoArgsConstructor
@SuperBuilder
public class LNRevolvingCreditDetailMYDTO extends LNRevolvingCreditDetailDTO {
    private String drawdownAmount;
    private String outstandingBalanceAmount;
    private String productName;
    private String productType;
    private String revolvingCreditType;
    private String workItemNumber;
    private String maturityDate; // format: dd MMM yyyy
}
