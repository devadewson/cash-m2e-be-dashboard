package com.m2e.dashboard.mapper.af.pbr;

import com.m2e.dashboard.dto.af.response.pbr.AFPBRUnitSummaryDTO;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRViewByAccountDTO;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRViewByFacilityDTO;
import com.m2e.dashboard.jms.af.pbr.response.account.details.ESBAFPBRAccountDTO;
import com.m2e.dashboard.jms.af.pbr.response.account.details.ESBAFPBRAccountDetailDTO;
import com.m2e.dashboard.jms.af.pbr.response.account.details.ESBAFPBRUnitDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ESBAFPBRAcctBalanceInquiryMapper {

    ESBAFPBRAcctBalanceInquiryMapper INSTANCE = Mappers.getMapper(ESBAFPBRAcctBalanceInquiryMapper.class);

    @Mapping(source = "unitNumber", target = "acctNumber")
    @Mapping(source = "currency", target = "accountCurrency")
    @Mapping(source = "region", target = "provider")
    @Mapping(target = "financedAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "settlementAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "amountDue", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"penaltyAmount\"))")
    AFPBRViewByAccountDTO mapToSummaryResp(ESBAFPBRUnitDTO dto);

    @Mapping(source = "accountNumber", target = "masterAcctNumber")
    @Mapping(source = "currency", target = "accountCurrency")
    @Mapping(source = "region", target = "provider")
    @Mapping(target = "totalFacilityLimit", expression = "java(mapAcctDetails(dto.getDetails(), \"facilityLimit\"))")
    @Mapping(target = "totalSettlementAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmount\"))")
    AFPBRViewByFacilityDTO mapToMasterSummaryResp(ESBAFPBRAccountDTO dto);

    @Mapping(source = "unitNumber", target = "unitNumber")
    @Mapping(source = "currency", target = "accountCurrency")
    @Mapping(target = "financedAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "settlementAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "amountDue", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"penaltyAmount\"))")
    AFPBRUnitSummaryDTO mapToUnitSummaryResp(ESBAFPBRUnitDTO dto);

    @Named("mapAcctDetails")
    default String mapAcctDetails(List<ESBAFPBRAccountDetailDTO> details, String name) {
        if (details == null || name == null)
            return null;

        String value = details.stream()
                .filter(detail -> detail != null && detail.getName() != null && name.equalsIgnoreCase(detail.getName()))
                .map(detail -> detail.getValue() != null ? detail.getValue() : "")
                .findFirst()
                .orElse(null);

        return value != null ? value.replace("+", "") : null;
    }

}