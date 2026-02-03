package com.m2e.dashboard.mapper.loan.tlf;

import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFAccountDTO;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFDetailDTO;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
@Slf4j
public class ESBLoanTLFBaseMapper {

    protected static String mapAcctDetails(ESBLoanTLFAccountDTO dto, String label) {
        return mapAcctDetails(dto.getDetails(), label);
    }

    protected static String mapAcctDetails(List<ESBLoanTLFDetailDTO> details, String label) {
        if (label != null) {
            return details.stream()
                    .filter(detail -> label.equalsIgnoreCase(detail.getName()))
                    .map(ESBLoanTLFDetailDTO::getValue)
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    private static BigDecimal getBigDecimal(List<ESBLoanTLFDetailDTO> details, String amountLabel, String signLabel) {
        String sign = "+";
        if (signLabel != null)
            sign = details.stream()
                    .filter(detail -> signLabel.equals(detail.getName()))
                    .map(ESBLoanTLFDetailDTO::getValue)
                    .findFirst()
                    .orElse(sign);

        BigDecimal amount = null;
        try {
            amount = details.stream()
                    .filter(detail -> amountLabel.equalsIgnoreCase(detail.getName()))
                    .map(detail -> new BigDecimal(detail.getValue()))
                    .findFirst()
                    .orElse(null);
        } catch (Exception ignored) {
            log.warn("getBigDecimal: Malformed value for label: '{}'", amountLabel);
        }

        if (amount == null) {
            return null;
        } else if (sign.equals("+")) {
            return amount;
        } else if (sign.equals("-")) {
            // Business requirement: Ignore negative signs, treat as positive
            return amount;
        } else {
            log.warn("getBigDecimal: Abnormal sign '{}', defaulting to positive", sign);
            return amount;
        }
    }

    private static String sumOf(List<ESBLoanTLFDetailDTO> details, Map<String, String> labelAndSigns) {
        String sum = labelAndSigns.entrySet().stream()
                .map(entry -> {
                    BigDecimal amountValue = getBigDecimal(details, entry.getKey(), entry.getValue());
                    if (amountValue == null) {
                        log.warn("sumOf component: ({}, sign={}) is missing or null, excluded from calculation", entry.getKey(), entry.getValue());
                        return BigDecimal.ZERO; // Exclude from calculation
                    }
                    log.info("sumOf component: ({}, sign={}) = {}", entry.getKey(), entry.getValue(), amountValue);
                    return amountValue;
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add)
                .setScale(2, RoundingMode.HALF_UP)
                .toString();
        log.info("sumOf result: {}", sum);
        return sum;
    }

    @SuppressWarnings("java:S1192")
    public static String mapMYOutstandingAmount(List<ESBLoanTLFDetailDTO> details, ESBServiceType serviceType) {
        if (serviceType == null) {
            log.warn("mapMYOutstandingAmount: serviceType is null");
            return null;
        }
        return switch (serviceType) {
            case LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_CONV,
                 LOAN_TLF_ACCT_DETAILS_MY_CONV -> {
                // Product code: LNS
                Map<String, String> labelAndSigns = new HashMap<>();
                labelAndSigns.put("monthProgInt", null); // Interest Due
                labelAndSigns.put("instalmentDue", "signInstalmentDue"); // Instalment Due
                labelAndSigns.put("miscCharges", null); // Miscellaneous Charges
                labelAndSigns.put("gstOS", null); // Outstanding GST
                labelAndSigns.put("penaltyDBAmt", null); // Penalty Debit
                labelAndSigns.put("interestOnMisc", null); // Interest on Miscellaneous
                yield sumOf(details, labelAndSigns);
            }
            case LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_ISLAMIC,
                 LOAN_TLF_ACCT_DETAILS_MY_ISLAMIC -> {
                // Product code: ILA
                Map<String, String> labelAndSigns = new HashMap<>();
                labelAndSigns.put("profitonCOF", null); // Outstanding Profit on Cost
                labelAndSigns.put("GPPOS", null); // Outstanding Grace Profit Period
                labelAndSigns.put("instalmentDue", "signInstalmentDue"); // Instalment Due
                labelAndSigns.put("gstOS", null); // Outstanding GST
                labelAndSigns.put("miscCharges", null); // Miscellaneous Charges
                labelAndSigns.put("commitFee", null); // Outstanding Compensation Fee
                yield sumOf(details, labelAndSigns);
            }
            case LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_FOREIGN_CONV,
                 LOAN_TLF_ACCT_DETAILS_MY_FOREIGN_CONV -> {
                // Product code: FCL
                Map<String, String> labelAndSigns = new HashMap<>();
                labelAndSigns.put("instalmentDue", "signInstalmentDue"); // Instalment Due
                labelAndSigns.put("interstDue", null); // Interest Due
                labelAndSigns.put("penaltyONPrincipal", null); // Penalty Interest on Principal
                labelAndSigns.put("penaltyOnInterest", null); // Penalty Interest on Interest
                labelAndSigns.put("miscCharges", null); // Miscellaneous Charges
                labelAndSigns.put("commitmentFees", null); // Outstanding Commitment Fees
                yield sumOf(details, labelAndSigns);
            }
            case LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_FOREIGN_ISLAMIC,
                 LOAN_TLF_ACCT_DETAILS_MY_FOREIGN_ISLAMIC -> {
                // Product code: FCF
                Map<String, String> labelAndSigns = new HashMap<>();
                labelAndSigns.put("profitPymtDue", null); // Profit Payment Due
                labelAndSigns.put("instalmentDue", "signInstalmentDue"); // Instalment Due
                labelAndSigns.put("miscCharges", null); // Miscellaneous Charges
                labelAndSigns.put("penaltyONPrincipal", null); // Compensation Fee on COF
                labelAndSigns.put("penaltyOnInterest", null); // Compensation Fee on Profit
                yield sumOf(details, labelAndSigns);
            }
            default -> {
                log.warn("mapMYOutstandingAmount: serviceType provided is not handled");
                yield null;
            }
        };
    }
}
