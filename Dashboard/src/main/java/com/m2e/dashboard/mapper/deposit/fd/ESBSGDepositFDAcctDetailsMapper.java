package com.m2e.dashboard.mapper.deposit.fd;

import com.m2e.dashboard.dto.deposit.fd.response.DepositFDAccountDetailDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDetailDTO;
import com.m2e.dashboard.util.ESBValueUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ESBSGDepositFDAcctDetailsMapper {

    ESBSGDepositFDAcctDetailsMapper INSTANCE = Mappers.getMapper(ESBSGDepositFDAcctDetailsMapper.class);

    @Mapping(target = "principalAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"principalAmount\"))")
    @Mapping(target = "noOfCertificates", expression = "java(mapAcctDetails(dto.getDetails(), \"certsCount\"))")
    DepositFDAccountDetailDTO mapToDepositFDDetailDTO(ESBDepositCAAccountDTO dto);

    default String mapAcctDetails(List<ESBDepositCAAccountDetailDTO> details, String acctDetail) {
        return details.stream()
                .filter(detail -> detail != null && detail.getName() != null && acctDetail.equalsIgnoreCase(detail.getName()))
                .map(detail -> {
                    if ("principalAmount".equalsIgnoreCase(detail.getName())) {
                        return ESBValueUtil.formatSgEsbAmountValue(detail.getValue());
                    } else if ("certsCount".equalsIgnoreCase(detail.getName())) {
                        return stripLeadingZeros(detail.getValue());
                    } else {
                        return detail.getValue();
                    }
                })
                .findFirst()
                .orElse(null);
    }

    default String stripLeadingZeros(String value) {
        if (value == null) return null;
        String stripped = value.replaceFirst("^0+(?!$)", "");
        return stripped.isEmpty() ? "0" : stripped;
    }
}
