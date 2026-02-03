package com.m2e.dashboard.mapper.loan.tlf;

import com.m2e.dashboard.dto.loan.tlf.response.acctdetail.*;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.enums.LNProductType;
import com.m2e.dashboard.enums.esb.termloan.my.PaymentOptionEnum;
import com.m2e.dashboard.enums.esb.termloan.my.RepaymentFreqEnum;
import com.m2e.dashboard.enums.esb.termloan.sg.PaymentTypeEnum;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFAccountDTO;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFDetailDTO;
import com.m2e.dashboard.util.ESBValueUtil;
import com.m2e.utils.common.DateTimeUtil;
import com.m2e.utils.common.enums.DateTimeFormatPatternEnum;
import org.mapstruct.Context;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.util.StringUtils;

import java.util.List;

@Mapper(componentModel = "spring")
public abstract class ESBLoanTLFAcctDetailsMapper {

    private static final String DEFAULT_AMT = "0.00";
    private static final String DASH = "-";

    @Mapping(target = "approvedAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"amountApproved\"))")
    @Mapping(target = "drawdownAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"amountReleased\"))")
    @Mapping(target = "outstandingBalanceAmount", expression = "java(getAmtWithSign(dto.getDetails(), \"ledgerBal\", \"signLedgerBal\"))")
    @Mapping(target = "productType", expression = "java(mapProductType(dto.getDetails(), \"islamicFlag\"))")
    @Mapping(target = "term", expression = "java(mapAcctDetails(dto.getDetails(), \"term\"))")
    @Mapping(target = "outstandingAmount", source = "details", qualifiedByName = "mapMYOutstandingAmount")
    @Mapping(target = "installmentAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"instalmentAmt\"))")
    @Mapping(target = "installmentDueAmount", expression = "java(getAmtWithSign(dto.getDetails(), \"instalmentDue\", \"signInstalmentDue\"))")
    @Mapping(target = "installmentDueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"principalPymtDueDate\"))")
    @Mapping(target = "installmentFrequency", expression = "java(mapRepaymentFreq(dto.getDetails(), \"principalRepymtFreq\"))")
    @Mapping(target = "interestRate", expression = "java(mapAcctDetails(dto.getDetails(), \"interestRate\"))")
    @Mapping(target = "interestDueAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"monthProgInt\"))")
    @Mapping(target = "interestDueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"interestDueDate\"))")
    @Mapping(target = "interestFrequency", expression = "java(mapRepaymentFreq(dto.getDetails(), \"interestPaymentFreq\"))")
    @Mapping(target = "firstDrawdownDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"firstDrawDownDate\"))")
    @Mapping(target = "lastDrawdownDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"lastDrawDownDate\"))")
    @Mapping(target = "paymentOption", expression = "java(mapPaymentOption(dto.getDetails(), \"paymtOption\"))")
    @Mapping(target = "paymentInAdvanceAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"creditPayIn\"))")
    @Mapping(target = "miscellaneousChargesAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"miscCharges\"))")
    @Mapping(target = "interestOnMiscellaneous", expression = "java(mapAcctDetails(dto.getDetails(), \"interestOnMisc\"))")
    @Mapping(target = "penaltyInterest", expression = "java(mapAcctDetails(dto.getDetails(), \"penaltyDBAmt\"))") // todo: to confirm mapping with bank
    @Mapping(target = "commitmentFeesAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"commitFee\"))")
    @Mapping(target = "sstDueAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"gstOS\"))")
    protected abstract LNTLFDetailConventionalDTO mapToLoanAccountDetailMYConvResp(ESBLoanTLFAccountDTO dto, @Context ESBServiceType serviceType); // LNS

    public LNTLFDetailConventionalDTO mapToLoanAccountDetailMYConvResp(ESBLoanTLFAccountDTO dto) {
        return mapToLoanAccountDetailMYConvResp(dto, ESBServiceType.LOAN_TLF_ACCT_DETAILS_MY_CONV);
    }

    @Mapping(target = "approvedAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"amountApproved\"))")
    @Mapping(target = "drawdownAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"amountReleased\"))")
    @Mapping(target = "outstandingBalanceAmount", expression = "java(getAmtWithSign(dto.getDetails(), \"ledgerBal\", \"signLedgerBal\"))")
    @Mapping(target = "productType", expression = "java(mapProductType(dto.getDetails(), \"islamicFlag\"))")
    @Mapping(target = "term", expression = "java(mapAcctDetails(dto.getDetails(), \"term\"))")
    @Mapping(target = "outstandingAmount", source = "details", qualifiedByName = "mapMYOutstandingAmount")
    @Mapping(target = "installmentAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"instalmentAmt\"))")
    @Mapping(target = "installmentDueAmount", expression = "java(getAmtWithSign(dto.getDetails(), \"instalmentDue\", \"signInstalmentDue\"))")
    @Mapping(target = "installmentDueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"principalPymtDueDate\"))")
    @Mapping(target = "installmentFrequency", expression = "java(mapRepaymentFreq(dto.getDetails(), \"principalRepymtFreq\"))")
    @Mapping(target = "profitRate", expression = "java(mapAcctDetails(dto.getDetails(), \"profitRate\"))")
    @Mapping(target = "profitFrequencyRolloverPrd", expression = "java(mapRepaymentFreq(dto.getDetails(), \"profitPaymentFreq\"))")
    @Mapping(target = "profitDueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"profitPayDue\"))")
    @Mapping(target = "outstandingGraceProfitPrd", expression = "java(mapAcctDetails(dto.getDetails(), \"GPPOS\"))")
    @Mapping(target = "outstandingCostOfFinanceAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"principalOS\"))")
    @Mapping(target = "outstandingCompensationFee", expression = "java(mapAcctDetails(dto.getDetails(), \"commitFee\"))")
    @Mapping(target = "firstDrawdownDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"firstDrawDownDate\"))")
    @Mapping(target = "lastDrawdownDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"lastDrawDownDate\"))")
    @Mapping(target = "paymentOption", expression = "java(mapPaymentOption(dto.getDetails(), \"paymtOption\"))")
    @Mapping(target = "paymentInAdvanceAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"creditPayIn\"))")
    @Mapping(target = "sellingPriceAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"sellingPrice\"))")
    @Mapping(target = "profitOnCostOfFundsAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"profitonCOF\"))")
    @Mapping(target = "miscellaneousChargesAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"miscCharges\"))")
    @Mapping(target = "latePaymentChargesAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"penaltyDBAmt\"))")
    @Mapping(target = "hasSellingPrice", expression = "java(getHasSellingPrice(dto.getDetails()))")
    protected abstract LNTLFDetailIslamicDTO mapToLoanAccountDetailMYIslamicResp(ESBLoanTLFAccountDTO dto, @Context ESBServiceType serviceType); // ILA

    public LNTLFDetailIslamicDTO mapToLoanAccountDetailMYIslamicResp(ESBLoanTLFAccountDTO dto) {
        return mapToLoanAccountDetailMYIslamicResp(dto, ESBServiceType.LOAN_TLF_ACCT_DETAILS_MY_ISLAMIC);
    }

    @Mapping(target = "approvedAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"amountApproved\"))")
    @Mapping(target = "drawdownAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"amountReleased\"))")
    @Mapping(target = "outstandingBalanceAmount", expression = "java(getAmtWithSign(dto.getDetails(), \"ledgerBal\", \"signLedgerBal\"))")
    @Mapping(target = "productType", expression = "java(mapProductType(dto.getDetails(), \"islamicFlag\"))")
    @Mapping(target = "term", expression = "java(mapAcctDetails(dto.getDetails(), \"term\"))")
    @Mapping(target = "outstandingAmount", source = "details", qualifiedByName = "mapMYOutstandingAmount")
    @Mapping(target = "installmentAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"instalmentAmt\"))")
    @Mapping(target = "installmentDueAmount", expression = "java(getAmtWithSign(dto.getDetails(), \"instalmentDue\", \"signInstalmentDue\"))")
    @Mapping(target = "installmentDueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"profitPaymentDueDate\"))")
    @Mapping(target = "installmentFrequency", expression = "java(mapRepaymentFreq(dto.getDetails(), \"principalRepaymentFreq\"))")
    @Mapping(target = "interestDueAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"interstDue\"))")
    @Mapping(target = "interestDueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"profitPaymentDueDate\"))")
    @Mapping(target = "interestFrequency", expression = "java(mapRepaymentFreq(dto.getDetails(), \"profitRepaymentFreq\"))")
    @Mapping(target = "firstDrawdownDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"firstDrawDownDate\"))")
    @Mapping(target = "lastDrawdownDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"lastDrawDownDate\"))")
    @Mapping(target = "bulletMaturityDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"bulletMaturityDate\"))")
    @Mapping(target = "paymentOption", expression = "java(mapPaymentOption(dto.getDetails(), \"paymtOption\"))")
    @Mapping(target = "paymentInAdvanceAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"creditPayIn\"))")
    @Mapping(target = "penaltyOnPrincipal", expression = "java(mapAcctDetails(dto.getDetails(), \"penaltyONPrincipal\"))")
    @Mapping(target = "penaltyOnInterest", expression = "java(mapAcctDetails(dto.getDetails(), \"penaltyOnInterest\"))")
    @Mapping(target = "outstandingCommitmentFees", expression = "java(mapAcctDetails(dto.getDetails(), \"commitmentFees\"))")
    @Mapping(target = "miscellaneousChargesAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"miscCharges\"))")
    @Mapping(target = "interestOnMiscellaneous", expression = "java(mapAcctDetails(dto.getDetails(), \"commitmentFees\"))")   // todo: check mapping
    protected abstract LNTLFDetailMYForeignConventionalDTO mapToLoanAccountDetailMYForeignConvResp(ESBLoanTLFAccountDTO dto, @Context ESBServiceType serviceType); // FCL

    public LNTLFDetailMYForeignConventionalDTO mapToLoanAccountDetailMYForeignConvResp(ESBLoanTLFAccountDTO dto) {
        return mapToLoanAccountDetailMYForeignConvResp(dto, ESBServiceType.LOAN_TLF_ACCT_DETAILS_MY_FOREIGN_CONV);
    }

    @Mapping(target = "approvedAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"amountApproved\"))")
    @Mapping(target = "drawdownAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"amountReleased\"))")
    @Mapping(target = "outstandingBalanceAmount", expression = "java(getAmtWithSign(dto.getDetails(), \"ledgerBal\", \"signLedgerBal\"))")
    @Mapping(target = "productType", expression = "java(mapProductType(dto.getDetails(), \"islamicFlag\"))")
    @Mapping(target = "term", expression = "java(mapAcctDetails(dto.getDetails(), \"term\"))")
    @Mapping(target = "outstandingAmount", source = "details", qualifiedByName = "mapMYOutstandingAmount")
    @Mapping(target = "installmentAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"instalmentAmt\"))")
    @Mapping(target = "installmentDueAmount", expression = "java(getAmtWithSign(dto.getDetails(), \"instalmentDue\", \"signInstalmentDue\"))")
    @Mapping(target = "installmentDueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"profitPaymentDueDate\"))")
    @Mapping(target = "installmentFrequency", expression = "java(mapRepaymentFreq(dto.getDetails(), \"principalRepaymentFreq\"))")
    @Mapping(target = "profitPaymentDueAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"profitPymtDue\"))")
    @Mapping(target = "profitDueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"profitPaymentDueDate\"))")
    @Mapping(target = "outstandingCostOfFinanceAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"principalOutstanding\"))")
    @Mapping(target = "firstDrawdownDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"firstDrawDownDate\"))")
    @Mapping(target = "lastDrawdownDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"lastDrawDownDate\"))")
    @Mapping(target = "bulletMaturityDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"bulletMaturityDate\"))")
    @Mapping(target = "paymentOption", expression = "java(mapPaymentOption(dto.getDetails(), \"paymtOption\"))")
    @Mapping(target = "paymentInAdvanceAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"creditPayIn\"))")
    @Mapping(target = "sellingPriceAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"sellingPrice\"))")
    @Mapping(target = "profitOnCostOutstandingAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"oustandingPOC\"))")
    @Mapping(target = "compensationFeeOnCOF", expression = "java(mapAcctDetails(dto.getDetails(), \"penaltyONPrincipal\"))")
    @Mapping(target = "compensationFeeOnProfit", expression = "java(mapAcctDetails(dto.getDetails(), \"penaltyOnInterest\"))")
    @Mapping(target = "miscellaneousChargesAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"miscCharges\"))")
    @Mapping(target = "hasSellingPrice", expression = "java(getHasSellingPrice(dto.getDetails()))")
    protected abstract LNTLFDetailMYForeignIslamicDTO mapToLoanAccountDetailMYForeignIslamicResp(ESBLoanTLFAccountDTO dto, @Context ESBServiceType serviceType); // FCF

    public LNTLFDetailMYForeignIslamicDTO mapToLoanAccountDetailMYForeignIslamicResp(ESBLoanTLFAccountDTO dto) {
        return mapToLoanAccountDetailMYForeignIslamicResp(dto, ESBServiceType.LOAN_TLF_ACCT_DETAILS_MY_FOREIGN_ISLAMIC);
    }

    @Mapping(target = "approvedAmount", expression = "java(mapSGAcctDetailsNominal(dto.getDetails(), \"releaseAmt\"))") // todo: check mapping
    @Mapping(target = "drawdownAmount", expression = "java(mapSGAcctDetailsNominal(dto.getDetails(), \"releaseAmt\"))") // todo: check mapping
    @Mapping(target = "outstandingBalanceAmount", source = "details", qualifiedByName = "mapSGOutstandingBalance")
    @Mapping(target = "productType", expression = "java(mapProductType(dto.getDetails(), \"islamicFlag\"))")
    @Mapping(target = "monthlyInstallmentAmount", expression = "java(mapInstallmentAmount(dto.getDetails(), \"paymentAmount\"))")
    @Mapping(target = "nextPaymentDueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"nextPymtDueDate\"))")
    @Mapping(target = "lastPaymentDate", expression = "java(mapPaddedZeroesToPositiveInt(dto.getDetails(), \"lastPaymentDate\"))")
    @Mapping(target = "interestAmount", expression = "java(mapSGAcctDetailsNominal(dto.getDetails(), \"monthlyprogressInt\"))")
    @Mapping(target = "defaultLateInterest", expression = "java(mapSGAcctDetailsNominal(dto.getDetails(), \"lateCharges\"))")
    @Mapping(target = "paymentType", expression = "java(mapPaymentType(dto.getDetails(), \"paymentType\"))") // leave these field empty: defaultLateInterest, totalPrincipalAmountBilledAmount
    @Mapping(target = "totalPrincipalAmountBilledAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"\"))") // todo: check mapping
    public abstract LNTLFDetailSGSIBSDTO mapToLoanAccountDetailSGSIBSConvResp(ESBLoanTLFAccountDTO dto);

    @Mapping(target = "approvedAmount", expression = "java(mapSGAcctDetailsNominal(dto.getDetails(), \"releaseAmt\"))") // todo: check mapping
    @Mapping(target = "drawdownAmount", expression = "java(mapSGAcctDetailsNominal(dto.getDetails(), \"releaseAmt\"))") // todo: check mapping
    @Mapping(target = "outstandingBalanceAmount", source = "details", qualifiedByName = "mapSGOutstandingBalance")
    @Mapping(target = "productType", expression = "java(mapProductType(dto.getDetails(), \"islamicFlag\"))")
    @Mapping(target = "monthlyInstallmentAmount", expression = "java(mapInstallmentAmount(dto.getDetails(), \"paymentAmount\"))")
    @Mapping(target = "nextPaymentDueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"nextPymtDueDate\"))")
    @Mapping(target = "lastPaymentDate", expression = "java(mapPaddedZeroesToPositiveInt(dto.getDetails(), \"lastPaymentDate\"))")
    @Mapping(target = "interestAmount", expression = "java(mapSGAcctDetailsNominal(dto.getDetails(), \"monthlyprogressInt\"))")
    @Mapping(target = "defaultLateInterest", expression = "java(mapSGAcctDetailsNominal(dto.getDetails(), \"lateCharges\"))")
    @Mapping(target = "paymentType", expression = "java(mapPaymentType(dto.getDetails(), \"paymentType\"))") // leave these field empty: defaultLateInterest, totalPrincipalAmountBilledAmount
    @Mapping(target = "totalPrincipalAmountBilledAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"\"))") // todo: check mapping
    public abstract LNTLFDetailSGSIBSDTO mapToLoanAccountDetailSGSIBSIslamicResp(ESBLoanTLFAccountDTO dto);

    @Named("mapMYOutstandingAmount")
    protected String mapMYOutstandingAmount(List<ESBLoanTLFDetailDTO> details, @Context ESBServiceType serviceType) {
        return ESBLoanTLFBaseMapper.mapMYOutstandingAmount(details, serviceType);
    }

    @Named("mapSGOutstandingBalance")
    protected String mapSGOutstandingBalance(List<ESBLoanTLFDetailDTO> details) {
        return details.stream()
                .filter(detail -> "outstandingBal".equalsIgnoreCase(detail.getName()))
                .map(detail -> ESBValueUtil.formatSgEsbAmountValue(detail.getValue()))
                .findFirst()
                .orElse(null);
    }

    protected String mapInstallmentAmount(List<ESBLoanTLFDetailDTO> details, String acctDetail) {
        boolean tLastInd = details.stream()
                .filter(detail -> "tlastInd".equalsIgnoreCase(detail.getName()))
                .map(detail -> "Y".equals(detail.getValue()))
                .findFirst()
                .orElse(false);

        if (tLastInd) return null;

        return details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(detail -> ESBValueUtil.formatSgEsbAmountValue(detail.getValue()))
                .findFirst()
                .orElse(null);
    }

    protected String mapSGAcctDetailsNominal(List<ESBLoanTLFDetailDTO> details, String acctDetail) {
        if (acctDetail!=null) {
            return details.stream()
                    .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                    .map(detail -> ESBValueUtil.formatSgEsbAmountValue(detail.getValue()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    protected String mapPaddedZeroesToPositiveInt(List<ESBLoanTLFDetailDTO> details, String acctDetail) {
        if (acctDetail!=null) {
            return details.stream()
                    .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                    .map(detail -> ESBValueUtil.paddedZeroesToPositiveInt(detail.getValue()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    protected String getAmtWithSign(List<ESBLoanTLFDetailDTO> details, String balAmtLabel, String balSignLabel) {
        String ledgerBalSign = details.stream()
                .filter(detail -> balSignLabel.equals(detail.getName()))
                .map(ESBLoanTLFDetailDTO::getValue)
                .findFirst()
                .orElse("+");

        if (ledgerBalSign.equals("-")) {
            return details.stream()
                    .filter(detail -> balAmtLabel.equalsIgnoreCase(detail.getName()))
                    .map(ESBLoanTLFDetailDTO::getValue)
                    .findFirst()
                    .orElse(DEFAULT_AMT);
        }
        return DEFAULT_AMT;
    }

    protected boolean getHasSellingPrice(List<ESBLoanTLFDetailDTO> details){
        return details.stream()
                .filter(detail -> "sellingPrice".equalsIgnoreCase(detail.getName()))
                .map(detail -> !detail.getValue().isBlank())
                .findFirst()
                .orElse(false);
    }

    protected String mapAcctDetails(List<ESBLoanTLFDetailDTO> details, String acctDetail) {
        if (acctDetail!=null)
            return details.stream()
                    .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                    .map(detail -> detail.getValue() != null ? detail.getValue() : "")
                    .findFirst()
                    .orElse(null);
        else return null;
    }

    protected String mapAcctDetailsDate(List<ESBLoanTLFDetailDTO> details, String acctDetail) {
        if (acctDetail == null) {
            return DASH;
        }

        String esbDateString = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBLoanTLFDetailDTO::getValue)
                .findFirst()
                .orElse(null);
        if (!StringUtils.hasText(esbDateString) || esbDateString.equals("0") || esbDateString.equals("000000")) {
            return DASH;
        }

        String formatted = DateTimeUtil.formatDate(
                esbDateString, DateTimeFormatPatternEnum.yyyyMMdd, DateTimeFormatPatternEnum.dd_MMM_yyyy);
        if (formatted == null) {
            return DASH;
        }
        return formatted;
    }

    protected String mapRepaymentFreq(List<ESBLoanTLFDetailDTO> details, String acctDetail) {
        if (acctDetail!=null) {
            String interestRepaymentCode = details.stream()
                    .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                    .map(ESBLoanTLFDetailDTO::getValue)
                    .findFirst()
                    .orElse(null);
            return RepaymentFreqEnum.getDescriptionByCode(interestRepaymentCode);
        }
        else return null;
    }

    protected String mapPaymentOption(List<ESBLoanTLFDetailDTO> details, String acctDetail) {
        if (acctDetail!=null) {
            String paymentOptionCode = details.stream()
                    .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                    .map(ESBLoanTLFDetailDTO::getValue)
                    .findFirst()
                    .orElse(null);
            return PaymentOptionEnum.getDescriptionByCode(paymentOptionCode);
        }
        else return null;
    }

    protected String mapPaymentType(List<ESBLoanTLFDetailDTO> details, String acctDetail) {
        if (acctDetail!=null) {
            String paymentTypeCode = details.stream()
                    .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                    .map(ESBLoanTLFDetailDTO::getValue)
                    .findFirst()
                    .orElse(null);
            return PaymentTypeEnum.getDescriptionByCode(ESBValueUtil.paddedZeroesToPositiveInt(paymentTypeCode));
        }
        else return null;
    }

    protected LNProductType mapProductType(List<ESBLoanTLFDetailDTO> details, String acctDetail) {
        String isIslamic = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBLoanTLFDetailDTO::getValue)
                .findFirst()
                .orElse(null);

        if (isIslamic != null && isIslamic.equals("Y"))
            return LNProductType.ISLAMIC;
        else
            return LNProductType.CONVENTIONAL;
    }
}