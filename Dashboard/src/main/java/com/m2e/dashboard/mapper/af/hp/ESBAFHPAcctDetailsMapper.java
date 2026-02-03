package com.m2e.dashboard.mapper.af.hp;

import com.m2e.dashboard.dto.af.response.hp.AFHPAcctDetailDTO;
import com.m2e.dashboard.dto.af.response.hp.AFHPMasterAcctDetailDTO;
import com.m2e.dashboard.dto.af.response.hp.AFHPSGAcctDetailDTO;
import com.m2e.dashboard.enums.AFAcctType;
import com.m2e.dashboard.enums.LNProductType;
import com.m2e.dashboard.jms.af.bd.response.account.details.ESBAFBDAccountDetailDTO;
import com.m2e.dashboard.jms.af.hp.response.account.details.ESBAFHPAccountDTO;
import com.m2e.dashboard.jms.af.hp.response.account.details.ESBAFHPAccountDetailDTO;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFDetailDTO;
import com.m2e.dashboard.util.ESBValueUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface ESBAFHPAcctDetailsMapper {

    ESBAFHPAcctDetailsMapper INSTANCE = Mappers.getMapper(ESBAFHPAcctDetailsMapper.class);

    @Mapping(source = "accountNumber", target = "acctNumber")
    @Mapping(target = "acctName", expression = "java(mapAcctDetails(dto.getDetails(), \"accountName\"))")
    @Mapping(source = "currency", target = "currency")

    @Mapping(target = "financedAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "interestRate", expression = "java(mapAcctDetailsRate(dto.getDetails(), \"interestRate\"))")
    @Mapping(target = "dueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"dueDate\"))")
    @Mapping(target = "numberOfInstalmentPaid", expression = "java(mapAcctDetails(dto.getDetails(), \"noOfInstallmentPaid\"))")
    @Mapping(target = "vehicalReg", expression = "java(mapAcctDetails(dto.getDetails(), \"vehicleRegNumber\"))")
    @Mapping(target = "financingPeriod", expression = "java(mapAcctDetails(dto.getDetails(), \"financedPeriod\"))")
    @Mapping(target = "monthlyInstalment", expression = "java(mapAcctDetails(dto.getDetails(), \"monthlyInstallment\"))")
    @Mapping(target = "numberOfMonthsDue", expression = "java(mapAcctDetails(dto.getDetails(), \"noOfMonthsDue\"))")
    @Mapping(target = "instalmentDue", expression = "java(mapAcctDetails(dto.getDetails(), \"installmentDue\"))")
    @Mapping(target = "overdueInterest", expression = "java(mapAcctDetails(dto.getDetails(), \"overdueInterest\"))")
    @Mapping(target = "lateCharges", expression = "java(mapAcctDetails(dto.getDetails(), \"overdueInterest\"))")
    @Mapping(target = "otherCharges", expression = "java(mapAcctDetails(dto.getDetails(), \"otherCharges\"))")
    @Mapping(target = "settlementAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmountToday\"))")
    AFHPAcctDetailDTO mapToAccountDetailMYResp(ESBAFHPAccountDTO dto);

    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "instalmentAmt", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"installmentAmount\"))")
    @Mapping(target = "interestRate", expression = "java(mapAcctDetailsRate(dto.getDetails(), \"interestRate\"))")
    @Mapping(target = "dueDate", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"dueDate\"))")
    @Mapping(target = "numberOfInstalmentPaid", expression = "java(mapAcctDetails(dto.getDetails(), \"noOfInstallmentPaid\"))")
    @Mapping(target = "originalTenure", expression = "java(mapAcctDetails(dto.getDetails(), \"originalTenure\"))")
    @Mapping(target = "remainingTenure", expression = "java(mapAcctDetails(dto.getDetails(), \"remainingTenure\"))")
    @Mapping(target = "collateralIDCarPlate", expression = "java(mapAcctDetails(dto.getDetails(), \"collateralPlate\"))")
    @Mapping(target = "collateralInformation", expression = "java(mapAcctDetails(dto.getDetails(), \"collateralInformation\"))")
    @Mapping(target = "chassisNumber", expression = "java(mapAcctDetails(dto.getDetails(), \"chassisNo\"))")
    @Mapping(target = "engineNumber", expression = "java(mapAcctDetails(dto.getDetails(), \"engineNo\"))")
    @Mapping(target = "collateralDescription", expression = "java(mapAcctDetails(dto.getDetails(), \"collateralDescription\"))")
    @Mapping(target = "lateCharges", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"lateInterest\"))")
    @Mapping(target = "miscOutOfPocket", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"miscellaneous\"))")
    @Mapping(target = "accruedLateCharges", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"accrLateCharges\"))")
    @Mapping(target = "instalmentDue", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"installmentDue\"))")
    @Mapping(target = "monthlyInstalment", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"monthlyInstallment\"))")
    @Mapping(target = "amountDue", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"installmentAmountOverdue\"))")
    @Mapping(target = "originalLoanAmt", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "settlementAmt", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"settlementAmountToday\"))")
    @Mapping(target = "lateInterest", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"lateInterest\"))")
    @Mapping(target = "otherCharges", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"otherCharges\"))")
    @Mapping(target = "tlastIndicator", expression = "java(mapAcctDetails(dto.getDetails(), \"tlastInd\"))")
    AFHPSGAcctDetailDTO mapToAccountDetailSGResp(ESBAFHPAccountDTO dto);

    @Mapping(source = "accountNumber", target = "acctNumber")
    @Mapping(target = "acctName", expression = "java(mapAcctDetails(dto.getDetails(), \"accountName\"))")
    @Mapping(source = "currency", target = "currency")

    @Mapping(target = "acctType", expression = "java(mapAcctType(dto.getDetails(), \"productName\"))")
    @Mapping(target = "financedAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "facilityLimit", expression = "java(mapAcctDetails(dto.getDetails(), \"totalFacilityLimit\"))")
    @Mapping(target = "numberOfDaysAllowed", expression = "java(mapAcctDetails(dto.getDetails(), \"noOfDaysAllowed\"))")
    @Mapping(target = "totalPenalty", expression = "java(mapAcctDetails(dto.getDetails(), \"totalPenalty\"))")
    @Mapping(target = "totalUnitExpired", expression = "java(mapUnitExpired(dto.getDetails(), \"totalExpired\"))")
    @Mapping(target = "dateApproved", expression = "java(mapAcctDetailsDate(dto.getDetails(), \"dateApproved\"))")
    AFHPMasterAcctDetailDTO mapToAccountMasterDetailMYResp(ESBAFHPAccountDTO dto);

    default String mapAcctDetails(List<ESBAFHPAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String value = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFHPAccountDetailDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .findFirst()
                .orElse(null);

        return ESBValueUtil.removeLeadingPlus(value);
    }

    default String mapUnitExpired(List<ESBAFHPAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String value = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFHPAccountDetailDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .findFirst()
                .orElse(null);

        return "000".equalsIgnoreCase(value) ? "0" : value;
    }

    default String mapAcctDetailsAmount(List<ESBAFHPAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        return details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFHPAccountDetailDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .map(ESBValueUtil::formatSgEsbAmountValue)
                .findFirst()
                .orElse(null);
    }

    default String mapAcctDetailsRate(List<ESBAFHPAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;
        String rate =  details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFHPAccountDetailDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .findFirst()
                .orElse(null);

        return ESBValueUtil.formatToTwoDecimalPlaces(rate);
    }

    default String mapAcctDetailsDate(List<ESBAFHPAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String date = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFHPAccountDetailDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .findFirst()
                .orElse(null);

        return ESBValueUtil.convertDateFormat(date);
    }

    default String mapAcctType(List<ESBAFHPAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String isIslamic = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFHPAccountDetailDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .findFirst()
                .orElse(null);

        if (isIslamic != null && isIslamic.equals("ISLAMIC BLANKET HP"))
            return AFAcctType.ISLAMIC.getDescription();
        else
            return AFAcctType.CONVENTIONAL.getDescription();
    }



}