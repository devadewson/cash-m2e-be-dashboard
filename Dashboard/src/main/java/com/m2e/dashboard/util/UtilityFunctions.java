package com.m2e.dashboard.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.dto.common.BasePageRequest;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.repository.SysParamRepository;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class UtilityFunctions {

    private final ObjectMapper mapper;
    private final SysParamRepository sysParamRepository;

    public ZonedDateTime convertStringToZonedDateTime(String dateStr, String timeStr, String zoneId) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        LocalDate date = LocalDate.parse(dateStr, dateFormatter);
        LocalTime time = LocalTime.parse(timeStr, timeFormatter);
        LocalDateTime localDateTime = LocalDateTime.of(date, time);

        return ZonedDateTime.of(localDateTime, ZoneId.of(zoneId));
    }

    public String convertJSONtoString(Object obj) {
        String jsonStr = "";
        try {
            jsonStr = mapper.writeValueAsString(obj);

        } catch (IOException e) {
            log.error("Error: " + e.getStackTrace());
        }
        return jsonStr;
    }

    public static boolean convertSpBooleanResult(String value) {
        if (value != null) {
            return value.equalsIgnoreCase("Y");
        } else {
            return false;
        }
    }

    public static String maskTransactionRef(String transactionRef) {
        if (transactionRef == null || transactionRef.length() <= 4) {
            return transactionRef;
        }

        int lengthToMask = transactionRef.length() - 4;
        String maskedPart = "*".repeat(lengthToMask);
        String visiblePart = transactionRef.substring(lengthToMask);

        return maskedPart + visiblePart;
    }

    public static double safeParseDouble(String val) {
        if (val == null || val.trim().isEmpty())
            return 0.00;
        try {
            return Double.parseDouble(val.trim());
        } catch (NumberFormatException nfe) {
            return 0.00;
        }
    }

    public static String formatThousandsSeparatorAndCents(String value) {
        if (value == null) return null;
        BigDecimal bigDecimal;
        try {
            bigDecimal = new BigDecimal(value);
        } catch (Exception e) {
            log.error("formatThousandsSeparatorAndCents failed to parse {}", value, e);
            return value;
        }
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.US);
        DecimalFormat formatter = new DecimalFormat("#,##0.00", symbols);
        return formatter.format(bigDecimal);
    }

    public int getMaxAcctCount() {
        String systemParam = sysParamRepository.findByCd("MAX_ACCT_REALTIME").orElse(null);
        return systemParam != null ? Integer.parseInt(systemParam) : 0;
    }

    /**
     * Set pagination data for database SP-based list responses.
     */
    public static <T> void setPaginationData(
            BasePageRequest request,
            SearchCollectionResponse<T> result,
            BaseSpDto<?, ?> baseSpDto,
            List<T> dataList
    ) {
        result.setNextPage(
                request.getPage() >= baseSpDto.getTotalPage()
                        ? baseSpDto.getTotalPage()
                        : request.getPage() + 1
        );
        result.setCurrentPage(request.getPage());
        result.setTotalPages(baseSpDto.getTotalPage());
        result.setTotalRecordCount(baseSpDto.getTotalRecord());
        result.setData(dataList);
    }

    public static long minutesUntilNextTime(LocalTime targetTime, ZoneId zoneId) {
        ZonedDateTime now = ZonedDateTime.now(zoneId);
        ZonedDateTime targetDateTime = now.with(targetTime);

        // If the target time already passed today, move to tomorrow
        if (targetDateTime.isBefore(now)) {
            targetDateTime = targetDateTime.plusDays(1);
        }

        return ChronoUnit.MINUTES.between(now, targetDateTime);
    }
}