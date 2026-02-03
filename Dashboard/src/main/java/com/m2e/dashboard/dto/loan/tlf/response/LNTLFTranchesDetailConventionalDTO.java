package com.m2e.dashboard.dto.loan.tlf.response;

import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import lombok.Data;
import lombok.experimental.SuperBuilder;

@Data
public class LNTLFTranchesDetailConventionalDTO implements BaseESBMetadata {
    private String currency;
    private String trancheNumber;
    private String drawdownAmount;
    private String interestRate;
    private String interestPaymentFrequency;
    private String interestDueDate; // format: dd MMM yyyy
    private String interestDueAmount;
    private String penaltyOnInterestAmount;
    private ESBMetadataDTO esbMetadata = new ESBMetadataDTO();
}
