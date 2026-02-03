package com.m2e.dashboard.mapper.deposit.fd;

import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDetailDTO;
import com.m2e.dashboard.sp.deposit.fd.response.DepositFDAcctSummarySpDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ESBDepositFDAcctBalanceInquiryMapper {

    ESBDepositFDAcctBalanceInquiryMapper INSTANCE = Mappers.getMapper(ESBDepositFDAcctBalanceInquiryMapper.class);

    @Mapping(source = "accountNumber", target = "acctNumber")
    @Mapping(source = "currency", target = "currency")
    @Mapping(target = "noOfCertificates", source = "details", qualifiedByName = "mapCertsCount")
    @Mapping(target = "totalPrincipalAmt", source = "details", qualifiedByName = "mapPrincipalAmount")
    DepositFDAcctSummarySpDTO mapToDepositFDAcctSummaryDTO(ESBDepositCAAccountDTO dto);

    @Named("mapCertsCount")
    default String mapCertsCount(List<ESBDepositCAAccountDetailDTO> details) {
        return details.stream()
                .filter(detail -> detail != null && detail.getName() != null && "certsCount".equalsIgnoreCase(detail.getName()))
                .map(detail -> detail.getValue() != null ? detail.getValue() : "")
                .findFirst()
                .orElse(null);
    }

    @Named("mapPrincipalAmount")
    default String mapPrincipalAmount(List<ESBDepositCAAccountDetailDTO> details) {
        return details.stream()
                .filter(detail -> detail != null && detail.getName() != null && "principalAmount".equalsIgnoreCase(detail.getName()))
                .map(detail -> detail.getValue() != null ? detail.getValue() : "")
                .findFirst()
                .orElse(null);
    }
}
