package com.m2e.dashboard.mapper.deposit.ca;

import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDetailDTO;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ESBDepositCAAcctBalanceInquiryMapper {

    ESBDepositCAAcctBalanceInquiryMapper INSTANCE = Mappers.getMapper(ESBDepositCAAcctBalanceInquiryMapper.class);

    @Mapping(source = "accountNumber", target = "acctNumber")
    @Mapping(source = "currency", target = "currency")
    @Mapping(target = "acctName", source = "details", qualifiedByName = "mapAcctName")
    @Mapping(target = "ledgerBalance", source = "details", qualifiedByName = "mapLedgerBalance")
    @Mapping(target = "availableBalance", source = "details", qualifiedByName = "mapAvailableBalance")
    DepositAcctSummarySpDTO mapToDepositAcctSummaryResp(ESBDepositCAAccountDTO dto);

    @Named("mapAcctName")
    default String mapAcctName(List<ESBDepositCAAccountDetailDTO> details) {
        return "UNKNOWN SOURCE FIELD FROM ESB";
    }

    @Named("mapLedgerBalance")
    default String mapLedgerBalance(List<ESBDepositCAAccountDetailDTO> details) {
        if (details == null) {
            return null;
        }
        return details.stream()
                .filter(detail -> detail != null && detail.getName() != null && "ledgerBalance".equalsIgnoreCase(detail.getName()))
                .map(detail -> detail.getValue() != null ? detail.getValue() : "")
                .findFirst()
                .orElse(null);
    }

    @Named("mapAvailableBalance")
    default String mapAvailableBalance(List<ESBDepositCAAccountDetailDTO> details) {
        if (details == null) {
            return null;
        }
        return details.stream()
                .filter(detail -> detail != null && detail.getName() != null && "availableBalance".equalsIgnoreCase(detail.getName()))
                .map(detail -> detail.getValue() != null ? detail.getValue() : "")
                .findFirst()
                .orElse(null);
    }
}