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
public class LNTLFDetailIslamicDTO extends LNTLFDetailDTO implements BaseESBMetadata {
    private LNProductType productType;
    private String term;
    private String outstandingAmount;
    @Schema(name = "Installment *Amount*")
    private String installmentAmount;
    @Schema(name = "Installment *Due*")
    private String installmentDueAmount;
    @Schema(name = "Installment Due Date")
    private String installmentDueDate;
    private String installmentFrequency;
    private String profitRate;
    private String profitFrequencyRolloverPrd;
    private String profitDueDate;
    @Schema(name = "Outstanding Grace Profit Period")
    private String outstandingGraceProfitPrd;
    private String outstandingCostOfFinanceAmount;
    private String outstandingCompensationFee;
    private String firstDrawdownDate;
    private String lastDrawdownDate;
    private String paymentOption;
    private String paymentInAdvanceAmount;
    private String sellingPriceAmount;
    @Schema(name = "Outstanding Profit on Cost")
    private String profitOnCostOfFundsAmount;
    @Schema(name = "Miscellaneous Charges")
    private String miscellaneousChargesAmount;
    @Schema(name = "Outstanding Compensation Fee")
    private String latePaymentChargesAmount;
    private ESBMetadataDTO esbMetadata;
}
