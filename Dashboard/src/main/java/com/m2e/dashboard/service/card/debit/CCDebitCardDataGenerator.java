package com.m2e.dashboard.service.card.debit;

import com.m2e.dashboard.dto.card.debit.response.CCDebitCardListMYDTO;
import com.m2e.dashboard.dto.card.debit.response.CCDebitCardListSGDTO;
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
public class CCDebitCardDataGenerator {
    private Random random = new SecureRandom();
    private Random randomBD = new SecureRandom();

    public BigDecimal consolidatedValue() {
        return generateRandomBigDecimal(100000000, 2);
    }


    public List<CCDebitCardListMYDTO> debitCardMYListing(String currency) {
        List<CCDebitCardListMYDTO> results = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            results.add(dummyMYAccountListing("Maybank Visa Platinum Visa Debit Card", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), currency));
        }
        return results;
    }

    public List<CCDebitCardListSGDTO> debitCardSGListing(String currency) {
        List<CCDebitCardListSGDTO> results = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            results.add(dummySGAccountListing("Maybank Visa Platinum Debit Card", formatRandomNumber(generateRandomNumber(12)), generateRandomBigDecimal(1000000, 2), currency));
        }
        return results;
    }

    private CCDebitCardListMYDTO dummyMYAccountListing(String cardName, String cardNumber, BigDecimal limit, String currency) {
        return CCDebitCardListMYDTO.builder()
                .cardName(cardName)
                .cardHolderName("Javerline Ong Tsu Shian")
                .cardNumber(maskCreditCardAccList(cardNumber))
                .monthlyPurchasingLimit(limit.toString())
                .cardCurrency(currency)
                .build();
    }

    private CCDebitCardListSGDTO dummySGAccountListing(String cardName, String cardNumber, BigDecimal limit, String currency) {
        return CCDebitCardListSGDTO.builder()
                .cardName(cardName)
                .cardHolderName("Javerline Ong Tsu Shian")
                .cardNumber(maskCreditCardAccList(cardNumber))
                .reinstatementDate(getDate())
                .dailySpendingLimit(limit.toString())
                .monthlySpendingLimit(limit.toString())
                .cardCurrency(currency)
                .build();
    }

    public String lastUpdated() {
        OffsetDateTime offsetDateTime = OffsetDateTime.now(ZoneOffset.UTC);
        ZoneOffset gmtPlus8 = ZoneOffset.ofHours(8);
        OffsetDateTime adjustedTime = offsetDateTime.withOffsetSameInstant(gmtPlus8);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy. HH:mm:ss '(GMT+8)'");
        return adjustedTime.format(formatter);
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

    public static String maskCreditCardAccList(String debitCardNumber) {
        if (debitCardNumber == null || debitCardNumber.trim().isEmpty()) {
            return "";
        }

        int length = debitCardNumber.length();

        StringBuilder maskedNumber = new StringBuilder();

        maskedNumber.append("**** ");

        maskedNumber.append(debitCardNumber.substring(length - 4));

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
