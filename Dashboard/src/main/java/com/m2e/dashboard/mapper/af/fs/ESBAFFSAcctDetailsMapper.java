package com.m2e.dashboard.mapper.af.fs;

import com.m2e.dashboard.dto.af.response.fs.AFFSCertDetailDTO;
import com.m2e.dashboard.dto.af.response.fs.AFFSMasterAcctDetailDTO;
import com.m2e.dashboard.dto.af.response.fs.AFFSSGAcctDetailDTO;
import com.m2e.dashboard.enums.FloorStockPaymentType;
import com.m2e.dashboard.jms.af.fs.response.account.details.ESBAFFSAccountDTO;
import com.m2e.dashboard.jms.af.fs.response.account.details.ESBAFFSAccountDetailsDTO;
import com.m2e.dashboard.jms.af.fs.response.account.details.ESBAFFSCertDetailsDTO;
import com.m2e.dashboard.util.ESBValueUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ESBAFFSAcctDetailsMapper {

    ESBAFFSAcctDetailsMapper INSTANCE = Mappers.getMapper(ESBAFFSAcctDetailsMapper.class);

    @Mapping(target = "certNo", expression = "java(dto.getCertNumber())")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "agreementNumber", expression = "java(mapAcctDetails(dto.getDetails(), \"\"))")
    // todo: Pending completion of Figma
    @Mapping(target = "financedAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "interestCharged", expression = "java(mapAcctDetails(dto.getDetails(), \"interestCharge\"))")
    @Mapping(target = "amtDue", expression = "java(mapAcctDetails(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "settlementAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"redemptionAmount\"))")
    //todo: check mapping
    @Mapping(target = "solaStatus", expression = "java(mapAcctDetails(dto.getDetails(), \"solaStatus\"))")
    @Mapping(target = "expiryDate", expression = "java(mapAcctDetails(dto.getDetails(), \"expireDate\"))")
        // skipped conversion to acctName, companyName, lastUpdatedDateTime
    AFFSCertDetailDTO mapToCertDetailMYResp(ESBAFFSCertDetailsDTO dto);

    @Mapping(target = "acctNumber", source = "accountNumber")
    @Mapping(target = "facilityLimit", expression = "java(mapAcctDetails(dto.getDetails(), \"facilityLimit\"))")
    @Mapping(target = "financedAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "productName", constant = "Floor Stocking")
    @Mapping(target = "acctStatus", expression = "java(mapAcctDetails(dto.getDetails(), \"accountStatus\"))")
        // skipped conversion to acctName, currency, companyName, numberOfAcct, productType, lastUpdatedDateTime
    AFFSMasterAcctDetailDTO mapToAccountMasterDetailMYResp(ESBAFFSAccountDetailsDTO dto);

    @Mapping(target = "acctNumber", expression = "java(mapSGAcctNumber(dto.getAccountNumber()))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "interestAmt", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"interestAmount\"))")
    @Mapping(target = "interestRate", expression = "java(mapAcctDetailsRateSG(dto.getDetails(), \"interestRate\"))")
    @Mapping(target = "nextPaymentDate", expression = "java(mapAcctDetailsDateSG(dto.getDetails(), \"nextPaymentDueDate\"))")
    @Mapping(target = "paymentType", expression = "java(mapPaymentTypeFS(dto.getDetails(), \"paymentType\"))")
    @Mapping(target = "collateralId", expression = "java(mapAcctDetails(dto.getDetails(), \"collaterelPlate\"))")
    @Mapping(target = "collateralInformation", expression = "java(mapAcctDetails(dto.getDetails(), \"collaterelPlate\"))")
    @Mapping(target = "chassisNumber", expression = "java(mapAcctDetails(dto.getDetails(), \"chassisNo\"))")
    @Mapping(target = "engineNumber", expression = "java(mapAcctDetails(dto.getDetails(), \"engineNo\"))")
    @Mapping(target = "lastPaymentDate", expression = "java(mapAcctDetailsDateSG(dto.getDetails(), \"lastPaymentDate\"))")
    @Mapping(target = "lateInterestOverdue", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"lateInterestOverdueAmount\"))")
    @Mapping(target = "collateralDescription", expression = "java(mapAcctDetails(dto.getDetails(), \"collateralDescription\"))")
    @Mapping(target = "maturityDate", expression = "java(mapAcctDetailsDateSG(dto.getDetails(), \"maturityDate\"))")
    @Mapping(target = "misc", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"miscellaneous\"))")
    @Mapping(target = "otherCharges", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"otherCharges\"))")
    @Mapping(target = "accruedInterest", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"accruedInterest\"))")
    @Mapping(target = "accruedLateCharges", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"accruedLateCharges\"))")
    @Mapping(target = "maturityRate", expression = "java(mapAcctDetailsRateSG(dto.getDetails(), \"maturityRate\"))")
    @Mapping(target = "loanTerm", expression = "java(mapAcctDetails(dto.getDetails(), \"loanterm\"))")
    @Mapping(target = "financedAmt", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"financedAmount\"))")
    // for acct-list
    @Mapping(target = "lateChargeInterestRate", expression = "java(mapAcctDetailsRateSG(dto.getDetails(), \"lateChargesInterestRate\"))")
    @Mapping(target = "originalLoanAmt", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"originalLoanAmount\"))")
    @Mapping(target = "settlementAmt", expression = "java(mapAcctDetailsSG(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "tlastIndicator", expression = "java(mapAcctDetails(dto.getDetails(), \"tlastInd\"))")
    AFFSSGAcctDetailDTO mapToAccountDetailSGResp(ESBAFFSAccountDetailsDTO dto);

    default String mapAcctDetails(List<ESBAFFSAccountDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String value = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFFSAccountDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .findFirst()
                .orElse(null);

        return ESBValueUtil.removeLeadingPlus(value);
    }

    @Named("mapSGAcctNumber")
    default String mapSGAcctNumber(String accountNumber) {
        if (accountNumber == null)
            return null;

        return ESBValueUtil.removeLeadingZeroes(accountNumber);
    }

    default String mapAcctDetailsSG(List<ESBAFFSAccountDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        return details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFFSAccountDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .map(ESBValueUtil::formatSgEsbAmountValue)
                .findFirst()
                .orElse(null);
    }

    default String mapAcctDetailsRateSG(List<ESBAFFSAccountDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String rate = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFFSAccountDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .map(ESBValueUtil::convertToValidPercentage)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        return ESBValueUtil.formatToTwoDecimalPlaces(rate);
    }

    default String mapAcctDetailsDateSG(List<ESBAFFSAccountDTO> details, String acctDetail) {
        if (details == null || acctDetail == null) {
            return null;
        }

        String date = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFFSAccountDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .findFirst()
                .orElse(null);

        return ESBValueUtil.convertDateFormat(date);
    }

    default String mapPaymentTypeFS(List<ESBAFFSAccountDTO> details, String acctDetail) {
        if (details == null || acctDetail == null) {
            return null;
        }

        String paymentType = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFFSAccountDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .findFirst()
                .orElse(null);

        return Arrays.stream(FloorStockPaymentType.values())
                .filter(val -> val.name().equalsIgnoreCase(paymentType))
                .map(FloorStockPaymentType::getPaymentType)
                .findFirst()
                .orElse(paymentType);
    }

}