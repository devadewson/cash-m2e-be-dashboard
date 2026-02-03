package com.m2e.dashboard.mapper.deposit.fd;

import com.m2e.dashboard.dto.deposit.fd.response.DepositFDAccountDetailDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDetailDTO;
import com.m2e.dashboard.mapper.deposit.ca.ESBDepositCAAcctDetailsMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper(componentModel = "spring")
public interface ESBDepositFDAcctDetailsMapper {

    ESBDepositCAAcctDetailsMapper INSTANCE = Mappers.getMapper(ESBDepositCAAcctDetailsMapper.class);

    @Mapping(target = "principalAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"principalAmount\"))")
    @Mapping(target = "noOfCertificates", expression = "java(mapAcctDetails(dto.getDetails(), \"certsCount\"))")
    DepositFDAccountDetailDTO mapToDepositFDDetailDTO(ESBDepositCAAccountDTO dto);
    default String mapAcctDetails(List<ESBDepositCAAccountDetailDTO> details, String acctDetail) {
        return details.stream()
                .filter(detail -> detail != null && detail.getName() != null && acctDetail.equalsIgnoreCase(detail.getName()))
                .map(detail -> detail.getValue() != null ? detail.getValue() : "")
                .findFirst()
                .orElse(null);
    }
}
