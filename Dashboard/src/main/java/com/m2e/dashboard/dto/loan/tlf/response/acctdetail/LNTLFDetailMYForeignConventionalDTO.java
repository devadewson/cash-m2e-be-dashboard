package com.m2e.dashboard.dto.loan.tlf.response.acctdetail;

import com.m2e.dashboard.dto.loan.tlf.response.LNTLFDetailDTO;
import com.m2e.dashboard.enums.LNProductType;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.service.BaseESBMetadata;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class LNTLFDetailMYForeignConventionalDTO extends LNTLFDetailDTO implements BaseESBMetadata {
    private LNProductType productType;
    private String term;
    private String outstandingAmount;
    private String installmentAmount;
    private String installmentDueAmount;
    private String installmentDueDate;
    private String installmentFrequency;
    private String interestDueAmount;
    @Schema(name = "Interest Due Date")
    private String interestDueDate;
    private String interestFrequency;
    private String firstDrawdownDate;
    private String lastDrawdownDate;
    private String bulletMaturityDate;
    private String paymentOption;
    private String paymentInAdvanceAmount;
    @Schema(name = "Penalty on Principal")
    private String penaltyOnPrincipal;
    private String penaltyOnInterest;
    private String outstandingCommitmentFees;
    private String miscellaneousChargesAmount;
    private String interestOnMiscellaneous;
    private ESBMetadataDTO esbMetadata = new ESBMetadataDTO();
}
