package com.m2e.dashboard.mapper.loan.tlf;

import com.m2e.dashboard.dto.loan.tlf.response.LNTLFAccountStatisticDTO;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFAccountDTO;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFDetailDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;
import java.util.List;

@Mapper(componentModel = "spring")
public interface ESBLoanTLFAcctStatsMapper {

    ESBLoanTLFAcctStatsMapper INSTANCE = Mappers.getMapper(ESBLoanTLFAcctStatsMapper.class);

    default LNTLFAccountStatisticDTO mapToAcctSummaryResp(List<ESBLoanTLFAccountDTO> datas) {
        LNTLFAccountStatisticDTO lntlfAccountStatisticDTO = new LNTLFAccountStatisticDTO();

        String noOfAccounts = getNoOfAccounts(datas);
        String drawdownAmount = getDrawdownAmount(datas);
        String outstandingBalanceAmount = getOutstandingBalanceAmount(datas);

        lntlfAccountStatisticDTO.setNumberOfAccounts(Integer.valueOf(noOfAccounts));
        lntlfAccountStatisticDTO.setConsolidatedDrawdownAmount(drawdownAmount);
        lntlfAccountStatisticDTO.setConsolidatedOutstandingBalanceAmount(outstandingBalanceAmount);
        lntlfAccountStatisticDTO.setCurrency(getCurrency(datas));

        return lntlfAccountStatisticDTO;
    }


    default String getNoOfAccounts(List<ESBLoanTLFAccountDTO> accounts) {
        return Integer.toString(accounts.size());
    }

    default String getDrawdownAmount(List<ESBLoanTLFAccountDTO> accounts) {
        return accounts.stream()
                .filter(account -> account.getDetails() != null)  // Filter out accounts with null details
                .flatMap(account -> account.getDetails().stream())  // Flatten the details list
                .filter(detail -> "currentBalance".equals(detail.getName()))  // Filter for ledgerBalance
                .map(detail -> detail.getValue())  // Extract the value
                .filter(value -> value != null && !value.isEmpty())  // Filter out null or empty values
                .map(BigDecimal::new)  // Convert non-null and non-empty values to BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add)  // Sum all BigDecimal values
                .toString();
    }

    default String getOutstandingBalanceAmount(List<ESBLoanTLFAccountDTO> accounts) {
        return accounts.stream()
                .filter(account -> account.getDetails() != null)  // Filter out accounts with null details
                .flatMap(account -> account.getDetails().stream())  // Flatten the details list
                .filter(detail -> "ledgerBal".equals(detail.getName()))  // Filter for ledgerBalance
                .map(detail -> detail.getValue())  // Extract the value
                .filter(value -> value != null && !value.isEmpty())  // Filter out null or empty values
                .map(BigDecimal::new)  // Convert non-null and non-empty values to BigDecimal
                .reduce(BigDecimal.ZERO, BigDecimal::add)  // Sum all BigDecimal values
                .toString();
    }

    default String getCurrency(List<ESBLoanTLFAccountDTO> account) {
        return account.get(0).getDetails().stream()
                .filter(detail -> "currencyCode".equalsIgnoreCase(detail.getName()))
                .map(ESBLoanTLFDetailDTO::getValue)
                .findFirst()
                .orElse(null);
    }
}
