package com.m2e.dashboard.service.overview.Refactor;


import com.m2e.dashboard.dto.overview.response.OverviewSubResDTO;
import com.m2e.dashboard.enums.OvSubProductEnum;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Component
public class DepositAccountMapper {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");

    /**
     * Map list of BTS accounts to OverviewSubResDTO.
     */
    public List<OverviewSubResDTO> mapAll(List<DepositAcctSummarySpDTO> btsAccounts) {
        if (btsAccounts == null || btsAccounts.isEmpty()) {
            return List.of();
        }

        return btsAccounts.stream()
                .map(this::mapToOverviewSubResDTO)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    /**
     * Map single BTS account to OverviewSubResDTO.
     */
    public OverviewSubResDTO mapToOverviewSubResDTO(DepositAcctSummarySpDTO account) {
        if (account == null) {
            return null;
        }

        OvSubProductEnum subProduct = determineSubProduct(account);

        OverviewSubResDTO dto = new OverviewSubResDTO();
        dto.setSubProduct(subProduct);
        dto.setSubProductDesc(getSubProductDescription(subProduct));
        dto.setCurrency(account.getCurrency() != null ? account.getCurrency() : "IDR");
        dto.setTotalAmount(convertToBigDecimal(account.getAvailableBalance() != null ?
                account.getAvailableBalance() :
                account.getLedgerBalance()));
        dto.setTotNoOfAccounts("1");
        dto.setLabel(account.getAcctName() != null ?
                account.getAcctName() :
                account.getAcctNumber());
        dto.setAsOf(OffsetDateTime.now().format(DATE_FORMAT));
        dto.setFacilityLimit(BigDecimal.ZERO);

        return dto;
    }

    public OvSubProductEnum determineSubProduct(DepositAcctSummarySpDTO account) {
        if (account == null) {
            return OvSubProductEnum.DEPOSIT;
        }

        String acctType = account.getAcctType();
        String currency = account.getCurrency();
        boolean isForeignCurrency = currency != null && !"IDR".equalsIgnoreCase(currency);

        // If account type is available, use it for classification
        if (acctType != null && !acctType.trim().isEmpty()) {
            return classifyByAccountType(acctType.trim(), isForeignCurrency);
        }

        // Fallback: classify by currency only
        log.debug("Account type is null for account: {}, classifying by currency",
                account.getAcctNumber());

        if (isForeignCurrency) {
            return OvSubProductEnum.FCA;
        }

        return OvSubProductEnum.DEPOSIT;
    }


    private OvSubProductEnum classifyByAccountType(String acctType, boolean isForeignCurrency) {
        switch (acctType.toUpperCase()) {
            case "CA":
            case "CURRENT":
            case "CASA":
                return isForeignCurrency ? OvSubProductEnum.FCA : OvSubProductEnum.CA;

            case "SA":
            case "SAVINGS":
                return OvSubProductEnum.SA;

            case "MFCA":
            case "MUDHARABAH":
            case "ISLAMIC_SA":
            case "ISLAMIC_CA":
                return OvSubProductEnum.MFCA;

            case "FD":
            case "FIXED_DEPOSIT":
            case "FIXED":
                return OvSubProductEnum.FD;

            case "TD":
            case "TERM_DEPOSIT":
            case "TERM":
                return OvSubProductEnum.TD;

            case "FCA":
            case "FOREIGN_CURRENCY":
            case "FCA_ACCOUNT":
                return OvSubProductEnum.FCA;

            case "VA":
            case "VIRTUAL_ACCOUNT":
                return OvSubProductEnum.VA;

            default:
                log.debug("Unknown account type: {}, using fallback classification", acctType);
                return isForeignCurrency ? OvSubProductEnum.FCA : OvSubProductEnum.DEPOSIT;
        }
    }

    private String getSubProductDescription(OvSubProductEnum subProduct) {
        switch (subProduct) {
            case CA: return "Current Account";
            case SA: return "Savings Account";
            case MFCA: return "Mudharabah Current Account";
            case FCA: return "Foreign Currency Account";
            case FD: return "Fixed Deposit";
            case TD: return "Term Deposit";
            case VA: return "Virtual Account";
            case DEPOSIT: return "Other Deposit";
            default: return subProduct.name();
        }
    }

    private BigDecimal convertToBigDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.replace(",", "").trim());
        } catch (NumberFormatException e) {
            log.warn("Failed to convert '{}' to BigDecimal, returning ZERO", value);
            return BigDecimal.ZERO;
        }
    }
}
