package com.m2e.dashboard.mapper.af.hp;

import com.m2e.dashboard.dto.af.response.hp.AFHPAcctListDTO;
import com.m2e.dashboard.jms.af.hp.response.account.details.ESBAFHPAccountDTO;
import com.m2e.dashboard.jms.af.hp.response.account.details.ESBAFHPAccountDetailDTO;
import com.m2e.dashboard.util.ESBValueUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ESBAFHPAcctBalanceInquiryMapper {

    ESBAFHPAcctBalanceInquiryMapper INSTANCE = Mappers.getMapper(ESBAFHPAcctBalanceInquiryMapper.class);

    @Mapping(source = "accountNumber", target = "acctNumber")
    @Mapping(source = "currency", target = "currency")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "financedAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "instalmentDue", expression = "java(mapAcctDetails(dto.getDetails(), \"installmentDue\"))")
    @Mapping(target = "monthlyInstalment", expression = "java(mapAcctDetails(dto.getDetails(), \"monthlyInstallment\"))")
    @Mapping(target = "settlementAmt", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmountToday\"))")
    @Mapping(target = "otherCharges", expression = "java(mapAcctDetails(dto.getDetails(), \"otherCharges\"))")
    AFHPAcctListDTO mapToAFHPSummaryResp(ESBAFHPAccountDTO dto);

    @Mapping(target = "acctNumber", expression = "java(mapSGAcctNumber(dto.getAccountNumber()))")
    @Mapping(source = "currency", target = "currency")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"oustandingBalance\"))")
    @Mapping(target = "financedAmt", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "instalmentDue", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"installmentDue\"))")
    @Mapping(target = "monthlyInstalment", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"monthlyInstallment\"))")
    @Mapping(target = "instalmentAmt", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"installmentAmount\"))")
    @Mapping(target = "settlementAmt", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"settlementAmountToday\"))")
    @Mapping(target = "otherCharges", expression = "java(mapAcctDetailsAmount(dto.getDetails(), \"otherCharges\"))")
    @Mapping(target = "tlastIndicator", expression = "java(mapAcctDetails(dto.getDetails(), \"tlastInd\"))")
    AFHPAcctListDTO mapToAFHPSummarySGResp(ESBAFHPAccountDTO dto);

    default String mapAcctDetails(List<ESBAFHPAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String value = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFHPAccountDetailDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .findFirst()
                .orElse(null);

        //remove if got leading plus sign
        return ESBValueUtil.removeLeadingPlus(value);
    }

    @Named("mapSGAcctNumber")
    default String mapSGAcctNumber(String accountNumber) {
        if (accountNumber == null)
            return null;

        return ESBValueUtil.removeLeadingZeroes(accountNumber);
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
}