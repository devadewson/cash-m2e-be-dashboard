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
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ESBSGDepositCAAcctDetailsMapper {

    ESBSGDepositCAAcctDetailsMapper INSTANCE = Mappers.getMapper(ESBSGDepositCAAcctDetailsMapper.class);

    default void mapCommonDetails(ESBDepositCAAccountDTO dto, DepositAccountDetailDTO target) {
        target.setHoldAmount(ESBValueUtil.formatSgEsbAmountValue(mapAcctDetails(dto.getDetails(), "holdAmount")));
        target.setOdLimit(ESBValueUtil.formatSgEsbAmountValue(mapAcctDetails(dto.getDetails(), "facilityLimit")));
        target.setAuthODLimit(ESBValueUtil.formatSgEsbAmountValue(mapAcctDetails(dto.getDetails(), "facilityLimit")));
        target.setIntraDayODLimit(mapAcctDetails(dto.getDetails(), "intradayODLimit"));
        target.setUnUtilisedIntraDayOD(mapAcctDetails(dto.getDetails(), "unutilisedIntradayOD"));
        target.setUnUtilisedOD(ESBValueUtil.formatSgEsbAmountValue(mapAcctDetails(dto.getDetails(), "DrawdownAmount")));
        //float details
        String oneDayFloat = ESBValueUtil.formatSgEsbAmountValue(mapAcctDetails(dto.getDetails(), "oneDayFloat"));
        String twoDayFloat = ESBValueUtil.formatSgEsbAmountValue(mapAcctDetails(dto.getDetails(), "twoDayFloat"));
        String lateFloat   = ESBValueUtil.formatSgEsbAmountValue(mapAcctDetails(dto.getDetails(), "lateFloat"));
        target.setOneDayFloat(oneDayFloat);
        target.setTwoDayFloat(twoDayFloat);
        target.setOtherDayFloat(lateFloat);
        //set total float calculated from ESB
        target.setTotalFloat(ESBValueUtil.getSGDepositCAFloatSum(oneDayFloat, twoDayFloat, lateFloat));
    }

    default DepositAccountDetailDTO mapToDepositAccountDetail(ESBDepositCAAccountDTO dto) {
        DepositAccountDetailDTO target = new DepositAccountDetailDTO();
        mapCommonDetails(dto, target);
        target.setLedgerBalance(ESBValueUtil.formatSgEsbAmountValue(mapAcctDetails(dto.getDetails(), "ledgerBalance")));
        target.setAvailableBalance(ESBValueUtil.formatSgEsbAmountValue(mapAcctDetails(dto.getDetails(), "availableBalance")));
        target.setActualBalance(ESBValueUtil.formatSgEsbAmountValue(mapAcctDetails(dto.getDetails(), "currentBalance")));
        return target;
    }

    default DepositAccountDetailDTO mapToMFCADepositAccountDetail(ESBDepositCAAccountDTO dto) {
        DepositAccountDetailDTO target = new DepositAccountDetailDTO();
        mapCommonDetails(dto, target);
        target.setLedgerBalance(ESBValueUtil.formatSgEsbAmountValue(mapAcctDetails(dto.getDetails(), "currentBalance")));
        target.setAvailableBalance(ESBValueUtil.formatSgEsbAmountValue(mapAcctDetails(dto.getDetails(), "currentBalance")));
        target.setActualBalance(ESBValueUtil.formatSgEsbAmountValue(mapAcctDetails(dto.getDetails(), "currentBalance")));
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
                .map(detail -> detail.getValue() != null ? detail.getValue() : "")
                .findFirst()
                .orElse(null);
    }
}