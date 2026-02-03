package com.m2e.dashboard.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.m2e.dashboard.enums.*;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ESBValueUtil {

    private static final Logger log = LoggerFactory.getLogger(ESBValueUtil.class);

    public static String convertToValidPercentage(String rawAmount) {
        if (rawAmount == null || rawAmount.trim().isEmpty())
            return null;

        if (rawAmount.equals("0"))
            return "0";

        BigDecimal amount = new BigDecimal(rawAmount).movePointLeft(7);

        return amount.stripTrailingZeros().toPlainString();
    }

    public static String convertDateFormat(String date) {
        if (date == null || date.trim().isEmpty()) {
            return null;
        }

        if(date.equals("0.00") || date.equals("0") || date.equals("00000000")){
            return date;
        }

        try {
            String newDate = date.contains(".") ? date.replace(".", "") : date;
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
            LocalDate localDate = LocalDate.parse(newDate, inputFormatter);
            return localDate.format(formatter);
        } catch (Exception e) {
            log.info("convertDateFormat error -> {} ", e);
            // Optional: log the invalid date format
            return date;
        }
    }

    public static String convertDateFormat(String dateTime, DateTimeFormatPatternEnum inputPattern,
                                               DateTimeFormatPatternEnum outputPattern) {
        if (dateTime == null || dateTime.trim().isEmpty()) {
            return null;
        }

        if (dateTime.equals("0.00") || dateTime.equals("0") || dateTime.equals("00000000")) {
            return dateTime;
        }

        if (dateTime.length() != inputPattern.getDateFormatPattern().length()) {
            return dateTime;
        }

        try {
            String newDate = dateTime.contains(".") ? dateTime.replace(".", "") : dateTime;
            DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputPattern.getDateFormatPattern());
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(outputPattern.getDateFormatPattern());
            LocalDate localDate = LocalDate.parse(newDate, inputFormatter);
            return localDate.format(formatter);

        } catch (Exception e) {
            log.error(
                    "convertDateFormat error with input value {}, input format {} and output format {}",
                    dateTime,
                    inputPattern.getDateFormatPattern(),
                    outputPattern.getDateFormatPattern(),
                    e
            );
            return dateTime;
        }
    }
    
	public static String convertDateTimeFormat(String dateTime, DateTimeFormatPatternEnum inputPattern,
			DateTimeFormatPatternEnum outputPattern) {
		if (dateTime == null || dateTime.trim().isEmpty()) {
			return null;
		}

		if (dateTime.equals("0.00") || dateTime.equals("0") || dateTime.equals("00000000")) {
			return dateTime;
		}

		if (dateTime.length() != inputPattern.getDateFormatPattern().length()) {
			return dateTime;
		}

		try {
			String newDate = dateTime.contains(".") ? dateTime.replace(".", "") : dateTime;
			DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern(inputPattern.getDateFormatPattern());
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern(outputPattern.getDateFormatPattern());
			LocalDateTime localDate = LocalDateTime.parse(newDate, inputFormatter);
			return localDate.format(formatter);

		} catch (Exception e) {
            log.error(
                    "convertDateTimeFormat error with input value {}, input format {} and output format {}",
                    dateTime,
                    inputPattern.getDateFormatPattern(),
                    outputPattern.getDateFormatPattern(),
                    e
            );
			return dateTime;
		}
	}

    public static LocalDate convertDateFromStringToLocalDate(String date) {
        if (date == null || date.trim().isEmpty() || date.equals("00000000")) {
            return null;
        }

        try {
            String newDate = date.contains(".") ? date.replace(".", "") : date;
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            return LocalDate.parse(newDate, dateFormatter);
        } catch (Exception e) {
            log.error("convertDateFromStringToLocalDate error -> {} ", e);
            return null;
        }
    }


    public static String formatSgEsbAmountValue(String value) {

        try {
            if (value == null || value.trim().isEmpty()) {
                return "0.00"; //default value if null
            }
            BigDecimal number = new BigDecimal(value).movePointLeft(2);
            DecimalFormat df = new DecimalFormat("##############0.00");
            return df.format(number);
        } catch (NumberFormatException e) {
            log.info("Error while format esb amount value for FD cert esb response processor");
            return "0.00";
        }
    }

    public static String getSGDepositCAFloatSum(String oneDayFloat, String twoDayFloat, String lateFloat) {
        BigDecimal oneFloat = parseDecimal(oneDayFloat);
        BigDecimal twoFloat = parseDecimal(twoDayFloat);
        BigDecimal otherFloat = parseDecimal(lateFloat);

        BigDecimal total = oneFloat.add(twoFloat).add(otherFloat);
        return total.toPlainString();
    }

    private static BigDecimal parseDecimal(String value) {
        if (value == null || value.trim().isEmpty()) {
            return BigDecimal.ZERO;
        }
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            return BigDecimal.ZERO;
        }
    }

    public static String formatSgLoanEsbAmountValue(String value) {
        try {
            if (value == null || value.trim().isEmpty()) {
                return "0.00"; //default value if null
            }
            BigDecimal number = new BigDecimal(value).divide(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
            DecimalFormat df = new DecimalFormat("0.00");
            return df.format(number);
        } catch (NumberFormatException e) {
            log.info("Error while format esb amount value for FD cert esb response processor");
            return "0.00";
        }
    }

    public static String formatSgCAEsbAcctNumber(String acctNumber) {
        //remove exactly eight leading zeros from SG ESB response
        if (acctNumber == null || acctNumber.trim().isEmpty()) {
            return "";
        }
        return acctNumber.replaceFirst("^00000000", "");
    }

    public static String formatSgEsbAcctNumber(String acctNumber) {
        //remove exactly seven leading zeros from SG ESB response
        if (acctNumber == null || acctNumber.trim().isEmpty()) {
            return "";
        }
        return acctNumber.replaceFirst("^0000000", "");
    }

    public static String formatTenure(String tenure) {
        if (tenure == null || tenure.trim().isEmpty()) {
            return null;
        }

        try {
            long value = Long.parseLong(tenure.replaceFirst("^0+", ""));
            String unit = (value == 1) ? "Month" : "Months";
            return value + " " + unit;
        } catch (NumberFormatException e) {
            log.info("Error while format tenure for FD cert esb response processor");
            return null;
        }
    }

    public static String getInstructionMaturityValue(String code) {
        for (InstructionMaturity im : InstructionMaturity.values()) {
            if (im.name().equalsIgnoreCase(code)) {
                return im.getValue();
            }
        }
        return null;
    }

    public static String getInstructionMaturityMY(String codeStr) {
        if (codeStr == null || codeStr.trim().isEmpty()) {
            return null;
        }
        try {
            for (InstructionMaturityMY mode : InstructionMaturityMY.values()) {
                if (mode.getCode().equals(codeStr)) {
                    return mode.getDscp();
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }
        //return null if no match found
        return null;
    }

    public static String getInterestPaymentModeValueMY(String codeStr) {
        if (codeStr == null || codeStr.trim().isEmpty()) {
            return null;
        }
        try {
            for (InterestPaymentModeMY mode : InterestPaymentModeMY.values()) {
                if (mode.getCode().equals(codeStr)) {
                    return mode.getDscp();
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }
        //return null if no match found
        return null;
    }

    public static String getInterestPaymentModeValueSG(String codeStr) {
        if (codeStr == null || codeStr.trim().isEmpty()) {
            return null;
        }
        try {
            for (InterestPaymentModeSG mode : InterestPaymentModeSG.values()) {
                if (mode.getCode().equals(codeStr)) {
                    return mode.getDscp();
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }
        //return null if no match found
        return null;
    }

    public static String getInterestPaymentFrequencyValueMY(String codeStr) {
        if (codeStr == null || codeStr.trim().isEmpty()) {
            return null;
        }
        try {
            for (InterestPaymentFrequencyMY mode : InterestPaymentFrequencyMY.values()) {
                if (mode.getCode().equals(codeStr)) {
                    return mode.getDscp();
                }
            }
        } catch (NumberFormatException e) {
            return null;
        }
        //return null if no match found
        return null;
    }

    public static String removeLeadingZeroes(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return null;
        if (input.length() > 11)
            return trimmed.replaceFirst("^0+(?!$)", "");

        return input;
    }

    public static String removeLeadingPlus(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return null;
        if (input.startsWith("+"))
            return input.substring(1);

        return input;
    }

    public static String paddedZeroesToPositiveInt(String input) {
        if (input == null) return null;
        String trimmed = input.trim();
        if (trimmed.isEmpty()) return null;
        return trimmed.replaceFirst("^0+(?!$)", "");
    }

    private static boolean isAcctMatchedESB(String esbAcctNumber, String acctNumber) {
        if (esbAcctNumber == null || acctNumber == null) {
            return false;
        }
        return esbAcctNumber.equals(acctNumber);
    }

    public static boolean isMatchingAccountESB(String accountNumber, String currency, DepositAcctSummarySpDTO dto) {
        if (accountNumber == null || currency == null || dto.getCurrency() == null || dto.getAcctNumber() == null) {
            return false;
        }
        return isAcctMatchedESB(accountNumber, dto.getAcctNumber()) &&
                dto.getCurrency().equalsIgnoreCase(currency);
    }

    public static String formatToTwoDecimalPlaces(String value) {
        log.info("Before interest rate : {} ", value);
        if (value == null || value.trim().isEmpty()) {
            return value;
        }
        try {
            double parsedValue = Double.parseDouble(value);
            log.info("parsedValue for interest rate : {} ", String.format("%.2f", parsedValue));
            return String.format("%.2f", parsedValue);
        } catch (NumberFormatException e) {
            return value;
        }
    }
}
