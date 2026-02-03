package com.m2e.dashboard.mapper.af.pbr;

import com.m2e.dashboard.dto.af.response.pbr.AFPBRMasterAccountDetailDTO;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRSubaccountDetailDTO;
import com.m2e.dashboard.jms.af.bd.response.account.details.ESBAFBDAccountDetailDTO;
import com.m2e.dashboard.jms.af.hp.response.account.details.ESBAFHPAccountDetailDTO;
import com.m2e.dashboard.jms.af.pbr.response.account.details.ESBAFPBRAccountDTO;
import com.m2e.dashboard.jms.af.pbr.response.account.details.ESBAFPBRAccountDetailDTO;
import com.m2e.dashboard.jms.af.pbr.response.account.details.ESBAFPBRUnitDTO;
import com.m2e.dashboard.util.ESBValueUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ESBAFPBRAcctDetailsMapper {

    ESBAFPBRAcctDetailsMapper INSTANCE = Mappers.getMapper(ESBAFPBRAcctDetailsMapper.class);

    @Mapping(target = "numberOfDays", expression = "java(mapAcctDetails(dto.getDetails(), \"noOFDays\"))")
    @Mapping(source = "unitNumber", target = "unitNumber")
    @Mapping(target = "financedAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "settlementAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "purchasedPrice", expression = "java(mapAcctDetails(dto.getDetails(), \"purchasePrice\"))")
    @Mapping(target = "penaltyAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"penaltyAmount\"))")
    @Mapping(target = "amountDue", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "outstandingBalance", expression = "java(mapAcctDetails(dto.getDetails(), \"penaltyAmount\"))")
    @Mapping(target = "engineNumber", expression = "java(mapAcctDetails(dto.getDetails(), \"engineNo\"))")
    @Mapping(target = "chassisNumber", expression = "java(mapAcctDetails(dto.getDetails(), \"chassisNo\"))")
    @Mapping(target = "expiryDate", expression = "java(mapAcctDetails(dto.getDetails(), \"expireDate\"))")
    @Mapping(target = "unitStatus", expression = "java(mapAcctDetails(dto.getDetails(), \"unitStatus\"))")
    @Mapping(target = "dateFinanced", expression = "java(mapAcctDetails(dto.getDetails(), \"dateFinanced\"))")
    AFPBRSubaccountDetailDTO mapToUnitDetailMYResp(ESBAFPBRUnitDTO dto);

    @Mapping(target = "numberOfDays", expression = "java(mapAcctDetails(dto.getDetails(), \"daysAllowed\"))")
    @Mapping(target = "facilityLimit", expression = "java(mapAcctDetails(dto.getDetails(), \"facilityLimit\"))")
    @Mapping(target = "totalFinancedAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"financedAmount\"))")
    @Mapping(target = "totalSettlementAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"settlementAmount\"))")
    @Mapping(target = "consolidatedUnutilisedAmount", expression = "java(mapAcctDetails(dto.getDetails(), \"unutilisedAmount\"))")
    @Mapping(target = "totalPenalty", expression = "java(mapAcctDetails(dto.getDetails(), \"totalPenalty\"))")
    @Mapping(target = "totalUnitExpired", expression = "java(mapUnitExpired(dto.getDetails(), \"totalExpiredUnit\"))")
    @Mapping(target = "dateApproved", expression = "java(mapAcctDetails(dto.getDetails(), \"dateApproved\"))")
    @Mapping(target = "pbrStatus", expression = "java(mapAcctDetails(dto.getDetails(), \"PBRStatus\"))")
    AFPBRMasterAccountDetailDTO mapToAccountMasterDetailMYResp(ESBAFPBRAccountDTO dto);

    default String mapAcctDetails(List<ESBAFPBRAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String value = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFPBRAccountDetailDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .findFirst()
                .orElse(null);

        return ESBValueUtil.removeLeadingPlus(value);
    }

    default String mapUnitExpired(List<ESBAFPBRAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null)
            return null;

        String value = details.stream()
                .filter(detail -> acctDetail.equalsIgnoreCase(detail.getName()))
                .map(ESBAFPBRAccountDetailDTO::getValue)
                .filter(val -> val != null && !val.trim().isEmpty())
                .findFirst()
                .orElse(null);

        return "000".equalsIgnoreCase(value) ? "0" : value;
    }
}