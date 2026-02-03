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
public class LNTLFDetailMYForeignIslamicDTO extends LNTLFDetailDTO implements BaseESBMetadata {
    private LNProductType productType;
    private String term;
    private String outstandingAmount;
    private String installmentAmount;
    @Schema(name = "Installment *Due*")
    private String installmentDueAmount;
    @Schema(name = "Installment Due Date")
    private String installmentDueDate;
    @Schema(name = "Installment Frequency")
    private String installmentFrequency;
    @Schema(name = "Profit Payment Due")
    private String profitPaymentDueAmount;
    private String profitDueDate;
    @Schema(name = "Outstanding Cost of Finance")
    private String outstandingCostOfFinanceAmount;
    private String firstDrawdownDate;
    private String lastDrawdownDate;
    private String bulletMaturityDate;
    private String paymentOption;
    private String paymentInAdvanceAmount;
    private String sellingPriceAmount;
    @Schema(name = "Profit on Cost Outstanding")
    private String profitOnCostOutstandingAmount;
    @Schema(name = "Compensation Fee on Cost of Financing (Compensation Fee on COF)")
    private String compensationFeeOnCOF;
    @Schema(name = "Compensation Fee on Profit")
    private String compensationFeeOnProfit;
    @Schema(name = "Miscellaneous Charges")
    private String miscellaneousChargesAmount;
    private ESBMetadataDTO esbMetadata;
}
