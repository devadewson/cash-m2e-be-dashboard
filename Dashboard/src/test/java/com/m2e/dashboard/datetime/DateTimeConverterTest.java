package com.m2e.dashboard.datetime;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import com.m2e.dashboard.enums.DateTimeFormatPatternEnum;
import com.m2e.dashboard.util.DateTimeUtil;
import com.m2e.dashboard.util.ESBValueUtil;

public class DateTimeConverterTest {

    @Test
    public void DateTimeConvertStartDateTimeTest() {
        // Given
        String date = "2024-08-07";
        String time = "15:45:30";
        Boolean isStartTime = true;

        // When
        String result = DateTimeUtil.convert12HourDateTime(date, time, isStartTime);

        // Then
        String expected = "07-AUG-24 03.45.30.000000000 PM +08:00";
        assertEquals(expected, result);
    }

    @Test
    public void DateTimeConvertEndDateTimeTest() {
        // Given
        String date = "2024-08-07";
        String time = "15:45:30";
        Boolean isStartTime = false;

        // When
        String result = DateTimeUtil.convert12HourDateTime(date, time, isStartTime);

        // Then
        String expected = "07-AUG-24 03.45.30.999999999 PM +08:00";
        assertEquals(expected, result);
    }

    @Test
    public void EInvoiceDateFormatTest() {
        // Given
        String date = "2025-08-01";
        LocalDate startDate = LocalDate.parse(date);
        String result = startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        System.out.println("result" + result);

        LocalDate mdate = LocalDate.parse(result, DateTimeFormatter.ofPattern("yyyyMMdd"));
        result = mdate.format(DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH));

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale.ENGLISH);
        YearMonth yearMonth = YearMonth.parse(result.trim(), dateFormatter);
        System.out.println("yearMonth " + yearMonth);
    }
    
    
    @Test
    public void myTest() {
        // Given
    	String dateString = "20250905"; // YYYYMMDD
        String timeString = "214212";   // HHmmss (24-hour)
        String dateTimeStr = dateString + timeString;
        
        String resultStr = ESBValueUtil.convertDateTimeFormat(dateTimeStr,
				DateTimeFormatPatternEnum.yyyyMMddHHmmss, DateTimeFormatPatternEnum.dd_MMM_yyyy_HH_mm);
        
        DecimalFormat fourDecimalFormat = new DecimalFormat("0.000000");
        String unitStr = "100.000005";
        unitStr = (unitStr == null || unitStr.isEmpty() || unitStr.equals("0")) ? "0.000000" : unitStr;
        Double unit = Double.parseDouble(unitStr);
        String formattedUnit = fourDecimalFormat.format(unit);
        System.out.println("formattedUnit:"+formattedUnit);
      
    }

}
