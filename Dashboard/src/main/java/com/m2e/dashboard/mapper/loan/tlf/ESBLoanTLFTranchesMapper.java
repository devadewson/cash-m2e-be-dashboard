package com.m2e.dashboard.mapper.loan.tlf;

import com.m2e.dashboard.dto.loan.tlf.response.LNTLFTrancheDTO;
import com.m2e.dashboard.enums.esb.termloan.my.RepaymentFreqEnum;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFDetailDTO;
import com.m2e.dashboard.jms.loan.tlf.response.tranch.details.ESBLoanTLFTranchesTrnRecDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ESBLoanTLFTranchesMapper {
    ESBLoanTLFTranchesMapper INSTANCE = Mappers.getMapper(ESBLoanTLFTranchesMapper.class);

    @Mapping(target = "trancheNumber", expression = "java(mapAcctDetails(dto.getDetails(), \"tranchNo\"))")
    @Mapping(target = "rate", expression = "java(mapAcctDetails(dto.getDetails(), \"profitRate\"))")
    @Mapping(target = "paymentFrequency", expression = "java(mapPaymentFrequency(dto.getDetails(), \"profitPaymentFreq\"))")
    @Mapping(target = "drawdownAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"currentBalance\"))")
    @Mapping(target = "currency", source = "currency")
    LNTLFTrancheDTO mapToLoanTranchesRes(ESBLoanTLFTranchesTrnRecDTO dto, String currency);

    @Named("mapAcctDetails")
    default String mapAcctDetails(List<ESBLoanTLFDetailDTO> details, String acctDetail) {
        return details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBLoanTLFDetailDTO::getValue)
                .findFirst()
                .orElse(null);
    }

    default String mapPaymentFrequency(List<ESBLoanTLFDetailDTO> tranchKeyValueList, String key) {
        String rawValue = mapAcctDetails(tranchKeyValueList, key);
        return RepaymentFreqEnum.getDescriptionByCode(rawValue);
    }
}
