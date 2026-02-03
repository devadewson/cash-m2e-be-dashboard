package com.m2e.dashboard.mapper.af.bd;

import com.m2e.dashboard.dto.af.response.bd.*;
import com.m2e.dashboard.jms.af.bd.response.account.details.*;
import com.m2e.dashboard.util.ESBValueUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", imports = ESBValueUtil.class)
public interface ESBAFBDAcctDetailsMapper {

    ESBAFBDAcctDetailsMapper INSTANCE = Mappers.getMapper(ESBAFBDAcctDetailsMapper.class);

    @Mapping(target = "financedAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "numberOfInstalmentPaid", expression = "java(mapAcctDetails(dto.getDetails(), \"noOfInstallmentPaid\"))")
    @Mapping(target = "numberOfInstalmentInArreas", expression = "java(mapAcctDetails(dto.getDetails(), \"noOfInstallmentinArrears\"))")
    @Mapping(target = "installmentAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"\"))")
    @Mapping(target = "amountDue", expression = "java(mapAcctDetails(dto.getDetails(), \"amountInArrears\"))")
    @Mapping(target = "settlementAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "numberOfAgreement", expression = "java(mapAcctDetails(dto.getDetails(), \"noOFAgreement\"))")
    @Mapping(target = "dueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"dueDate\"))")
    @Mapping(target = "blockStatus", expression = "java(mapAcctDetails(dto.getDetails(), \"blockStatus\"))")
    @Mapping(target = "debitNote", expression = "java(mapAcctDetails(dto.getDetails(), \"\"))")
    AFBDAcctDetailDTO mapToBlockDetailMYResp(ESBAFBDBlockDetailDTO dto);

    @Mapping(target = "financedAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"OustandingBalance\"))")
    @Mapping(target = "facilityLimit", expression = "java(mapAcctDetails(dto.getDetails(), \"facilityLimit\"))")
    @Mapping(target = "acctStatus", expression = "java(mapAcctDetails(dto.getDetails(), \"accountStatus\"))")
    AFBDMasterAcctDetailDTO mapToMasterDetailMYResp(ESBAFBDAccountDTO dto);

    @Mapping(target = "financedAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "utilisedAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"utilisedAmount\"))")
    @Mapping(target = "numberOfInstalmentPaid", expression = "java(mapAcctDetails(dto.getDetails(), \"noOfInstallmentPaid\"))")
    @Mapping(target = "numberOfInstalmentInArreas", expression = "java(mapAcctDetails(dto.getDetails(), \"noOfInstallmentinArrears\"))")
    @Mapping(target = "installmentAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"installmentAmount\"))")
    @Mapping(target = "amountDue", expression = "java(mapAcctDetails(dto.getDetails(), \"amountInArrears\"))")
    @Mapping(target = "dueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"dueDate\"))")
    @Mapping(target = "settlementAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "agreementStatus", expression = "java(mapAcctDetails(dto.getDetails(), \"agreementStatus\"))")
    AFBDSubAcctDetailDTO mapToAgreementDetailMYResp(ESBAFBDAgreementDetailDTO dto);

    @Mapping(source = "debitNote", target = "debitNoteNumber")
    @Mapping(source = "accountNumber", target = "acctNumber")
    @Mapping(target = "outstandingBalance", expression = "java(ESBValueUtil.formatSgEsbAmountValue(dto.getDebitNoteAmount()))")
    AFBDDebitNoteDetailDTO mapToDebitNoteDetailMYResp(ESBAFBDDebitNoteDetailDTO dto);

    @Mapping(target = "financedAmt", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "numberOfInstalmentPaid", expression = "java(mapAcctDetails(dto.getDetails(), \"noOfInstallmentPaid\"))")
    @Mapping(target = "numberOfInstalmentInArreas", expression = "java(mapAcctDetails(dto.getDetails(), \"noOfInstallmentinArrears\"))")
    @Mapping(target = "numberOfAgreement", expression = "java(mapAcctDetails(dto.getDetails(), \"noOFSubAccount\"))")
    @Mapping(target = "dueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"dueDate\"))")
    @Mapping(target = "blockStatus", expression = "java(mapAcctDetails(dto.getDetails(), \"blockStatus\"))")
    @Mapping(target = "instalmentAmt", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"installmentAmount\"))")
    @Mapping(target = "amountDue", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"overdueAmount\"))")
    @Mapping(target = "interestRate", expression = "java(mapAcctDetailsRate(dto.getDetails(), \"interestRate\"))")
    @Mapping(target = "originalTenure", expression = "java(mapAcctDetails(dto.getDetails(), \"originalTenure\"))")
    @Mapping(target = "remainingTenure", expression = "java(mapAcctDetails(dto.getDetails(), \"remainingTenure\"))")
    @Mapping(target = "lateCharges", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"lateCharges\"))")
    @Mapping(target = "misc", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"miscellaneous\"))")
    @Mapping(target = "otherCharges", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"otherCharges\"))")
    @Mapping(target = "originalLoanAmt", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"originalLoanAmount\"))")
    @Mapping(target = "settlementAmount", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "instalmentAmtOverdue", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"overdueInstalmentAmount\"))")
    @Mapping(target = "tlastIndicator", expression = "java(mapAcctDetails(dto.getDetails(), \"tlastInd\"))")
    AFBDSGAcctDetailDTO mapToMasterDetailSGResp(ESBAFBDAccountDTO dto);

    @Mapping(target = "outstandingLoan", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "numberOfSubAcct", expression = "java(mapAcctDetails(dto.getDetails(), \"noOFSubAccount\"))")
    @Mapping(target = "instalmentAmt", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"installmentAmount\"))")
    @Mapping(target = "amountDue", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"overdueAmount\"))")
    @Mapping(target = "interestRate", expression = "java(mapAcctDetailsRate(dto.getDetails(), \"interestRate\"))")
    @Mapping(target = "dueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"dueDate\"))")
    @Mapping(target = "originalTenure", expression = "java(mapAcctDetails(dto.getDetails(), \"originalTenure\"))")
    @Mapping(target = "numberOfInstalmentPaid", expression = "java(mapAcctDetails(dto.getDetails(), \"noOfInstallmentPaid\"))")
    @Mapping(target = "remainingTenure", expression = "java(mapAcctDetails(dto.getDetails(), \"remainingTenure\"))")
    @Mapping(target = "lateInterest", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"lateInterest\"))")
    @Mapping(target = "lateCharges", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"lateCharges\"))")
    @Mapping(target = "misc", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"miscellaneous\"))")
    @Mapping(target = "otherCharges", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"otherCharges\"))")
    @Mapping(target = "chassisNumber", expression = "java(mapAcctDetails(dto.getDetails(), \"chassisNo\"))")
    @Mapping(target = "engineNumber", expression = "java(mapAcctDetails(dto.getDetails(), \"engineNo\"))")
    @Mapping(target = "collateralDescription", expression = "java(mapAcctDetails(dto.getDetails(), \"collateralDescription\"))")
    @Mapping(target = "originalLoanAmt", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"originalLoanAmount\"))")
    @Mapping(target = "settlementAmount", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "instalmentAmtOverdue", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"overdueInstalmentAmount\"))")
    @Mapping(target = "tlastIndicator", expression = "java(mapAcctDetails(dto.getDetails(), \"tlastInd\"))")
    AFBDSGSubAcctDetailDTO mapToSubDetailSGResp(ESBAFBDSubAccountDTO dto);

    default String mapAcctDetails(List<ESBAFBDAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String value = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFBDAccountDetailDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .findFirst()
                .orElse(null);

        return ESBValueUtil.removeLeadingPlus(value);
    }

    default String mapAcctDetailsAmount(List<ESBAFBDAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        return details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFBDAccountDetailDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .map(ESBValueUtil::formatSgEsbAmountValue)
                .findFirst()
                .orElse(null);
    }

    default String mapAcctDetailsRate(List<ESBAFBDAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String rate =  details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFBDAccountDetailDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .map(ESBValueUtil::convertToValidPercentage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return ESBValueUtil.formatToTwoDecimalPlaces(rate);
    }

    default String mapAcctDetailsDate(List<ESBAFBDAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String date = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFBDAccountDetailDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .findFirst()
                .orElse(null);

        return ESBValueUtil.convertDateFormat(date);
    }
}