package com.m2e.dashboard.mapper.deposit.ca;

import com.m2e.dashboard.dto.deposit.ca.response.DepositAccountDetailDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositCAAcctOmnibusDetailDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDetailDTO;
import com.m2e.dashboard.util.ESBValueUtil;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ESBDepositCAAcctDetailsMapper {

    ESBDepositCAAcctDetailsMapper INSTANCE = Mappers.getMapper(ESBDepositCAAcctDetailsMapper.class);

    default void mapCommonDetails(ESBDepositCAAccountDTO dto, DepositAccountDetailDTO target) {
        target.setOdLimit(mapAcctDetails(dto.getDetails(), "overDraftAmount"));
        target.setAuthODLimit(mapAcctDetails(dto.getDetails(), "facilityLimit"));
        target.setIntraDayODLimit(mapAcctDetails(dto.getDetails(), "intradayODLimit"));
        target.setUnUtilisedIntraDayOD(mapAcctDetails(dto.getDetails(), "unutilisedIntradayOD"));
        target.setUnUtilisedOD(mapAcctDetails(dto.getDetails(), "unutilisedAmount"));
        target.setUtilisedOD(mapAcctDetails(dto.getDetails(), "utilisedAmount"));
        target.setOneDayFloat(mapAcctDetails(dto.getDetails(), "oneDayFloat"));
        target.setTwoDayFloat(mapAcctDetails(dto.getDetails(), "twoDayFloat"));
        target.setOtherDayFloat(mapAcctDetails(dto.getDetails(), "oclateFloat"));
        target.setTotalFloat(mapAcctDetails(dto.getDetails(), "totalFloat"));
        target.setOneDayFloatExpiryDate(mapAcctDetails(dto.getDetails(), "oneDayFloatExpiry"));
        target.setTwoDayFloatExpiryDate(mapAcctDetails(dto.getDetails(), "twoDayFloatExpiry"));
    }

    default DepositAccountDetailDTO mapToDepositAccountDetail(ESBDepositCAAccountDTO dto) {
        DepositAccountDetailDTO target = new DepositAccountDetailDTO();
        mapCommonDetails(dto, target);
        target.setLedgerBalance(mapAcctDetails(dto.getDetails(), "ledgerBalance"));
        target.setAvailableBalance(mapAcctDetails(dto.getDetails(), "availableBalance"));
        target.setActualBalance(mapAcctDetails(dto.getDetails(), "customerOwnBalance"));
        target.setHoldAmount(mapAcctDetails(dto.getDetails(), "holdAmount"));
        return target;
    }

    default DepositAccountDetailDTO mapToMFCADepositAccountDetail(ESBDepositCAAccountDTO dto) {
        DepositAccountDetailDTO target = new DepositAccountDetailDTO();
        mapCommonDetails(dto, target);
        target.setLedgerBalance(mapAcctDetails(dto.getDetails(), "currentBalance"));
        target.setAvailableBalance(mapAcctDetails(dto.getDetails(), "currentBalance"));
        target.setActualBalance(mapAcctDetails(dto.getDetails(), "currentBalance"));
        target.setHoldAmount(mapAcctDetails(dto.getDetails(), "atHoldAmt"));
        return target;
    }

    @Mapping(target = "odLimit", expression = "java(mapAcctDetails(dto.getDetails(), \"omnibusODLimit\"))")
    @Mapping(target = "unUtilisedOD", expression = "java(mapAcctDetails(dto.getDetails(), \"unutilisedOmnibusOD\"))")
    DepositCAAcctOmnibusDetailDTO mapToDepositAccountOmnibusDTO(ESBDepositCAAccountDTO dto);

    default String mapAcctDetails(List<ESBDepositCAAccountDetailDTO> details, String acctDetail) {
        if (details == null || acctDetail == null) {
            return null;
        }
        return details.stream()
                .filter(detail -> detail != null && detail.getName() != null && acctDetail.equalsIgnoreCase(detail.getName()))
                .map(detail -> detail.getValue() != null ? ESBValueUtil.removeLeadingPlus(detail.getValue()) : "")
                .findFirst()
                .orElse(null);
    }

}