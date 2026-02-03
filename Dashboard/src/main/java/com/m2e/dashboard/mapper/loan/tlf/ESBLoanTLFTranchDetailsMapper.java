package com.m2e.dashboard.mapper.loan.tlf;

import com.m2e.dashboard.dto.loan.tlf.response.LNTLFTranchesDetailConventionalDTO;
import com.m2e.dashboard.dto.loan.tlf.response.LNTLFTranchesDetailIslamicDTO;
import com.m2e.dashboard.enums.esb.termloan.my.RepaymentFreqEnum;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ESBLoanTLFTranchDetailsMapper {

    ESBLoanTLFTranchDetailsMapper INSTANCE = Mappers.getMapper(ESBLoanTLFTranchDetailsMapper.class);

    @Mapping(target = "trancheNumber", expression = "java(mapTranchDetails(tranchKeyValueList, \"tranchNo\"))")
    @Mapping(target = "drawdownAmount", expression = "java(mapTranchDetailsWithSign(tranchKeyValueList, \"signCurrBal\", \"currentBalance\"))")
    @Mapping(target = "interestRate", expression = "java(mapTranchDetails(tranchKeyValueList, \"profitRate\"))")
    @Mapping(target = "interestPaymentFrequency", expression = "java(mapPaymentFrequency(tranchKeyValueList, \"profitPaymentFreq\"))")
    @Mapping(target = "interestDueDate", expression = "java(mapAndFormatDate(tranchKeyValueList, \"profitPaymentDueDate\"))")
    @Mapping(target = "interestDueAmount", expression = "java(mapTranchDetails(tranchKeyValueList, \"interestDue\"))")
    @Mapping(target = "penaltyOnInterestAmount", expression = "java(mapTranchDetails(tranchKeyValueList, \"penaltyOnInterest\"))")
    @Mapping(target = "currency", source = "currency")
    LNTLFTranchesDetailConventionalDTO mapToTermLoanTranchDetailDTO(List<ESBLoanTLFDetailDTO> tranchKeyValueList, String currency);

    @Mapping(target = "trancheNumber", expression = "java(mapTranchDetails(tranchKeyValueList, \"tranchNo\"))")
    @Mapping(target = "drawdownAmount", expression = "java(mapTranchDetailsWithSign(tranchKeyValueList, \"signCurrBal\", \"currentBalance\"))")
    @Mapping(target = "profitRate", expression = "java(mapTranchDetails(tranchKeyValueList, \"profitRate\"))")
    @Mapping(target = "profitPaymentFrequency", expression = "java(mapPaymentFrequency(tranchKeyValueList, \"profitPaymentFreq\"))")
    @Mapping(target = "profitDueDate", expression = "java(mapAndFormatDate(tranchKeyValueList, \"profitPaymentDueDate\"))")
    @Mapping(target = "profitDueAmount", expression = "java(mapTranchDetails(tranchKeyValueList, \"interestDue\"))")
    @Mapping(target = "latePaymentChargesOnProfitDueAmount", expression = "java(mapTranchDetails(tranchKeyValueList, \"penaltyOnInterest\"))")
    @Mapping(target = "currency", source = "currency")
    LNTLFTranchesDetailIslamicDTO mapToTermLoanTranchDetailIslamicDTO(List<ESBLoanTLFDetailDTO> tranchKeyValueList, String currency);

    default String mapTranchDetails(List<ESBLoanTLFDetailDTO> tranchKeyValueList, String key) {
        for (ESBLoanTLFDetailDTO keyValuePair : tranchKeyValueList) {
            if(key.equalsIgnoreCase(keyValuePair.getName())) {
                return keyValuePair.getValue();
            }
        }
        return null;
    }

    default String mapPaymentFrequency(List<ESBLoanTLFDetailDTO> tranchKeyValueList, String key) {
        String rawValue = mapTranchDetails(tranchKeyValueList, key);
        return RepaymentFreqEnum.getDescriptionByCode(rawValue);
    }

    default String mapTranchDetailsWithSign(List<ESBLoanTLFDetailDTO> tranchKeyValueList, String signLabel, String valueLabel) {
        String sign = "";
        for (ESBLoanTLFDetailDTO keyValuePair : tranchKeyValueList) {
            if(signLabel.equalsIgnoreCase(keyValuePair.getName())) {
                if ("-".equals(keyValuePair.getValue())) {
                    sign = "-";
                }
                break;
            }
        }
        for (ESBLoanTLFDetailDTO keyValuePair : tranchKeyValueList) {
            if(valueLabel.equalsIgnoreCase(keyValuePair.getName())) {
                return String.join(sign, keyValuePair.getValue());
            }
        }
        return null;
    }

    default String mapAndFormatDate(List<ESBLoanTLFDetailDTO> tranchKeyValueList, String dateLabel) {
        String esbDateString = tranchKeyValueList.stream()
                .filter(detail -> dateLabel.equalsIgnoreCase(detail.getName()))
                .map(ESBLoanTLFDetailDTO::getValue)
                .findFirst()
                .orElse(null);
        try {
            if (esbDateString!=null && !esbDateString.equals("0")) {
                LocalDate esbDate = LocalDate.parse(esbDateString, DateTimeFormatter.ofPattern("yyyyMMdd"));
                return esbDate.format(DateTimeFormatter.ofPattern("dd MMM yyyy"));
            }
        } catch (DateTimeParseException e) {
            return null;
        }
        return null;
    }
}