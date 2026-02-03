package com.m2e.dashboard.dto.deposit.fd.response;

import com.m2e.dashboard.dto.MoreActionsEntitlement;
import lombok.Data;

public class DepositFDCertificateDetailDTO {

    @Data
    public static class FullData extends RawData {
        private String certificateNumber;
        private String preferredName;
        private String principalAmount;

        private String currency;
        private String periodTerm;
        private String maturityInterest; // Upfront Profit in islamic
        private String certStatusDesc;
        private String principalCreditNumber;
        private String principalCreditAccountName;
        private String interestCreditNumber; // Profit Credit Account Number in islamic
        private String interestCreditAccountName;
        private String prematureClosureIndicator;
        private String lcrIndicator;

        // SG only fields
        private String upfrontInterestRatePA;
        private String interestOnInterestRatePA;
        private String totalInterest;
        private String upfrontInterest;
        private String interestOnInterestRate;
        private String totalInterestMaturity;

        private MoreActionsEntitlement moreActions;
        private RawData rawData;
    }

    @Data
    public static class RawData {
        private String tenure; // Term in islamic
        private String placementDate;
        private String maturityDate;
        private String lcrDate;
        private String instructionOnMaturity;
        private String interestPaymentMode; // Profit Payment Mode in islamic
        private String interestPaymentFrequency;
        private String interestRate; // Profit Rate in islamic
        private String profitSharingRatio;
        private String certStatusCode;
    }
}
