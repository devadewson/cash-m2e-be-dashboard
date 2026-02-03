package com.m2e.dashboard.service.card.charge;

import com.m2e.dashboard.dto.card.charge.response.CCChargeCardDetailDTO;
import com.m2e.dashboard.dto.card.charge.response.CCChargeCardListDTO;
import com.m2e.dashboard.dto.card.common.response.CCTrxHistoryDTO;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.StringJoiner;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class CCChargeCardDataGenerator {
    private Random random = new SecureRandom();
    private Random randomBD = new SecureRandom();
    private String companyName = "Petronas Sdn Bhd";

    public BigDecimal consolidatedValue() {
        return generateRandomBigDecimal(100000000, 2);
    }

    public List<CCChargeCardListDTO> chargeCardListing(String currency) {
        List<CCChargeCardListDTO> results = new ArrayList<>();
        results.add(dummyAccountListingAuthorized("Amex Green Corporate", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Amex Gold Corporate", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Amex Central Billing", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Amex Business Travel", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListingNewCard("MAS Amex Business Travel", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Amex Purchasing", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Amex Purchasing Virtual", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Amex Meeting", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListingRenewalCard("Amex Meeting Virtual", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Master Purchasing", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Visa Purchasing Islamic", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("MasterCard Purchasing Islamic", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Amex Green Corporate", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Amex Gold Corporate", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Amex Central Billing", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Amex Business Travel", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("MAS Amex Business Travel", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Amex Green Corporate", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Amex Meeting", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        results.add(dummyAccountListing("Master Purchasing", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), generateRandomBigDecimal(1000000, 2), currency));
        return results;
    }

    public List<CCTrxHistoryDTO> retrieveTrxHistory(String currency) {
        List<CCTrxHistoryDTO> results = new ArrayList<>();

        for (int x = 0; x < 5; x++) {
            results.add(dummyCreditTrxHistory(currency));
            results.add(dummyDebitTrxHistory(currency));
        }

        return results;
    }

    public List<CCTrxHistoryDTO> retrievePendingTrxHistory(String currency) {
        List<CCTrxHistoryDTO> results = new ArrayList<>();
        for (int x = 0; x < 10; x++) {
            results.add(dummyPendingTrxHistory(currency));
        }
        return results;
    }

    private CCTrxHistoryDTO dummyPendingTrxHistory(String currency) {
        return CCTrxHistoryDTO.builder()
                .trxDate(getDate())
                .trxDescription("Description " + generateRandomNumber(1))
                .debit(generateRandomBigDecimal(1000, 2).toString())
                .currency(currency)
                .build();
    }

    private CCTrxHistoryDTO dummyCreditTrxHistory(String currency) {
        return CCTrxHistoryDTO.builder()
                .trxDate(getDate())
                .trxDescription("Description " + generateRandomNumber(1))
                .credit(generateRandomBigDecimal(1000, 2).toString())
                .debit("-")
                .currency(currency)
                .build();
    }

    private CCTrxHistoryDTO dummyDebitTrxHistory(String currency) {
        return CCTrxHistoryDTO.builder()
                .trxDate(getDate())
                .trxDescription("Description " + generateRandomNumber(1))
                .credit("-")
                .debit(generateRandomBigDecimal(1000, 2).toString())
                .currency(currency)
                .build();
    }

    private CCChargeCardListDTO dummyAccountListing(String cardName, String cardNumber, BigDecimal limit, BigDecimal outStandingBalance, String currency) {
        return CCChargeCardListDTO.builder()
                .cardName(cardName)
                .cardHolderName("Javerline Ong Tsu Shian")
                .cardNumber(maskCreditCardAccList(cardNumber))
                .creditLimit(limit.toString())
                .outstandingBalance(outStandingBalance.toString())
                .cardCurrency(currency)
                .build();
    }

    private CCChargeCardListDTO dummyAccountListingNewCard(String cardName, String cardNumber, BigDecimal limit, BigDecimal outStandingBalance, String currency) {
        return CCChargeCardListDTO.builder()
                .cardName(cardName)
                .cardHolderName("Javerline Ong Tsu Shian")
                .cardNumber(maskCreditCardAccList(cardNumber))
                .creditLimit(limit.toString())
                .outstandingBalance(outStandingBalance.toString())
                .isNewCard(true)
                .cardCurrency(currency)
                .build();
    }

    private CCChargeCardListDTO dummyAccountListingRenewalCard(String cardName, String cardNumber, BigDecimal limit, BigDecimal outStandingBalance, String currency) {
        return CCChargeCardListDTO.builder()
                .cardName(cardName)
                .cardHolderName("Javerline Ong Tsu Shian")
                .cardNumber(maskCreditCardAccList(cardNumber))
                .creditLimit(limit.toString())
                .outstandingBalance(outStandingBalance.toString())
                .isRenewalCard(true)
                .cardCurrency(currency)
                .build();
    }

    private CCChargeCardListDTO dummyAccountListingAuthorized(String cardName, String cardNumber, BigDecimal limit, BigDecimal outStandingBalance, String currency) {
        return CCChargeCardListDTO.builder()
                .cardName(cardName)
                .cardHolderName("Javerline Ong Tsu Shian")
                .cardNumber(maskCreditCardAccList(cardNumber))
                .creditLimit(limit.toString())
                .outstandingBalance(outStandingBalance.toString())
                .isAuthorized(true)
                .cardCurrency(currency)
                .build();
    }

    public CCChargeCardDetailDTO getAccountDetail(String cardNumber, String cardCurrency, String cardName) {
        return CCChargeCardDetailDTO.builder()
                .cardName(cardName)
                .companyName(companyName)
                .cardHolderName("Javerline Ong Tsu Shian")
                .cardNumber(maskCreditCardAccDetail(cardNumber))
                .currency(cardCurrency)
                .lastUpdatedDateTime(lastUpdated())
                .outstandingBalance(generateRandomBigDecimal(1000000, 2).toString())
                .statementBalance(generateRandomBigDecimal(100000, 2).toString())
                .productName(cardName)
                .lastPaymentDate(getDate())
                .statementDate(getDate())
                .dueDate(getDate())
                .lastPaymentAmount(generateRandomBigDecimal(100000, 2).toString())
                .build();
    }

    public String getDate() {
        LocalDate startDate = LocalDate.now().minusMonths(3);

        LocalDate endDate = LocalDate.now();

        long startDays = startDate.toEpochDay();
        long endDays = endDate.toEpochDay();

        long randomDay = startDays + random.nextInt((int) (endDays - startDays + 1));

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        return LocalDate.ofEpochDay(randomDay).format(formatter);
    }

    public String lastUpdated() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);
        ZoneOffset gmtPlus8 = ZoneOffset.ofHours(8);
        OffsetDateTime adjustedTime = offsetDateTime.withOffsetSameInstant(gmtPlus8);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy. HH:mm:ss '(GMT+8)'");
        return adjustedTime.format(formatter);
    }

    private static String formatRandomNumber(String input) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int i = 0; i < input.length(); i += 4) {
            int end = Math.min(i + 4, input.length());
            joiner.add(input.substring(i, end));
        }
        return joiner.toString();
    }

    private BigDecimal generateRandomBigDecimal(int upperLimit, int decimalPlaces) {
        int integerValue = randomBD.nextInt(upperLimit);
        BigDecimal fractionalValue = BigDecimal.valueOf(randomBD.nextDouble()).setScale(decimalPlaces, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(integerValue).add(fractionalValue);
    }

    public static String maskCreditCardAccDetail(String creditCardNumber) {
        if (creditCardNumber == null || creditCardNumber.trim().isEmpty()) {
            return "";
        }

        int length = creditCardNumber.length();

        StringBuilder maskedNumber = new StringBuilder();

        maskedNumber.append("**** **** ");

        maskedNumber.append(creditCardNumber.substring(length - 4));

        return maskedNumber.toString();
    }

    public static String maskCreditCardAccList(String creditCardNumber) {
        if (creditCardNumber == null || creditCardNumber.trim().isEmpty()) {
            return "";
        }

        int length = creditCardNumber.length();

        StringBuilder maskedNumber = new StringBuilder();

        maskedNumber.append("**** ");

        maskedNumber.append(creditCardNumber.substring(length - 4));

        return maskedNumber.toString();
    }

    public String generateRandomNumber(int length) {
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom randomNumber = ThreadLocalRandom.current();
        sb.append(randomNumber.nextInt(1, 10));
        for (int i = 1; i < length; i++) {
            sb.append(randomNumber.nextInt(0, 10));
        }
        return sb.toString();
    }
}
