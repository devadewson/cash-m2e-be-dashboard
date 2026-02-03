package com.m2e.dashboard.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
public class DateTimeUtil {

    public static String getTMinus1Date(String format) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime yesterday = now.minusDays(1);
        ZoneId zoneId = ZoneId.of("Asia/Kuala_Lumpur");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format).withZone(zoneId);
        return yesterday.format(formatter);
    }

    public static String convert12HourDateTime(String date, String time, Boolean isStartTime) {
        String inputDate = date.concat(" ").concat(time);

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String nanoseconds = Boolean.TRUE.equals(isStartTime) ? "000000000" : "999999999";

        DateTimeFormatter outputFormatter = new DateTimeFormatterBuilder()
                .appendPattern("dd-MMM-yy hh.mm.ss")
                .appendLiteral(".")
                .appendLiteral(nanoseconds)
                .appendLiteral(" ")
                .appendPattern("a")
                .toFormatter(Locale.ENGLISH)
                .withLocale(Locale.ENGLISH);

        LocalDateTime dateTime = LocalDateTime.parse(inputDate, inputFormatter);
        OffsetDateTime offsetDateTime = dateTime.atOffset(ZoneOffset.ofHours(8));
        String formattedDate = offsetDateTime.format(outputFormatter);

        return String.format("%s %s", formattedDate, offsetDateTime.getOffset().toString()).toUpperCase();
    }

    public static String getCompactDateTime() {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyMMddHHmmss");
        LocalDateTime time = LocalDateTime.now();
        String date = time.format(format);

        String final5 = RandomStringUtils.random(5, false, true);
        return date + final5;
    }

    public static Map<String,String> getESBDefaultDateTime(){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(dateFormatter);

        String startTime = "000000";
        String endTime = "235959";

        String startDateTime = formattedDate + startTime;
        String endDateTime = formattedDate + endTime;

        Map<String, String> data = new HashMap<>();
        data.put("START_DT", startDateTime);
        data.put("END_DT", endDateTime);
        return data;
    }

    public static String getESBDateTime(String date, String time){
        String formattedDate = date.replace("-", "");
        String formattedTime = time.replace(":", "");
        return formattedDate + formattedTime;
    }

    public static Map<String,Integer> getESBDefaultTime(){
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HHmmss");

        LocalTime defaultStart = LocalTime.of(0,0,0);
        LocalTime defaultEnd = LocalTime.of(23,59,59);
        String formattedStartTime = defaultStart.format(timeFormatter);
        String formattedEndTime = defaultEnd.format(timeFormatter);

        Map<String, Integer> data = new HashMap<>();
        data.put("START_TM", Integer.valueOf(formattedStartTime));
        data.put("END_TM", Integer.valueOf(formattedEndTime));
        return data;
    }

    public static Map<String,Integer> getESBDefaultDate(){
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        LocalDate currentDate = LocalDate.now();
        String formattedDate = currentDate.format(dateFormatter);

        String startDateTime = formattedDate;
        String endDateTime = formattedDate;

        Map<String, Integer> data = new HashMap<>();
        data.put("START_DT", Integer.valueOf(startDateTime));
        data.put("END_DT", Integer.valueOf(endDateTime));
        return data;
    }

    public static Integer getESBDate(String date){
        String formattedDate = date.replace("-", "");
        return Integer.valueOf(formattedDate);
    }

    public static Integer getESBTime(String time){
        // convert HH:mm to HHmmss
        String formattedTime = time.replace(":", "");
        return Integer.valueOf(formattedTime);
    }

    public static String getTodayDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");
        String formattedDate = today.format(formatter);
        return formattedDate;
    }

    public static String getCurrentTime() {
        OffsetDateTime dateTime = OffsetDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss '(GMT'X')'");
        String formattedDateTime = dateTime.format(formatter);
        return formattedDateTime;
    }

    public static String formatESBFloatExpiryDate(String date) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        Date parsedDate = inputFormat.parse(date);
        return outputFormat.format(parsedDate);
    }

    public static String formatFloatExpiryDate(String date) throws ParseException {
        SimpleDateFormat inputFormat = new SimpleDateFormat("ddMMyyyy");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy");
        Date parsedDate = inputFormat.parse(date);
        return outputFormat.format(parsedDate);
    }

    public static String convertCATrxHistDate(String dateStr) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy 00:00");
        LocalDate date = LocalDate.parse(dateStr, inputFormatter);
        return date.format(outputFormatter);
    }

    public static String convertDepositCATrxHistDate(String dateStr, String timeStr) throws ParseException {
        log.info("dateStr: {}, timeStr: {}", dateStr, timeStr);
        //combine date and time into one string
        String paddedTime = String.format("%06d", Integer.parseInt(timeStr)); //make sure timeStr is always 6 digits valid
        String combined = dateStr + paddedTime;
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyyMMddHHmmss");
        Date date = inputFormat.parse(combined);
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd MMM yyyy HH:mm");
        String result = outputFormat.format(date);
        log.info("result time: {}", result);
        return result;
    }

    public static String convertProcessingTimeFormat(String timeStr) {
        if (timeStr == null || timeStr.length() != 6 || !timeStr.matches("\\d{6}")) {
            log.info("Invalid ESB value for Processing time for CA Activity : {}", timeStr);
            return "00:00:00";
        }

        String hours = timeStr.substring(0, 2);
        String minutes = timeStr.substring(2, 4);
        String seconds = timeStr.substring(4, 6);

        return hours + ":" + minutes + ":" + seconds;
    }


    public static String ensureTwoDigitFormat(String input) {
        // Check if the input length is valid (8 characters)
        if (input.length() == 7) {
            input = "0" + input;  // Prepend a zero for the single-digit day/month
        }
        return input;
    }

    public static String convertDateFormat(String date) {
        String newDate = date;

        if (date == null || date.equals("0.00") || !date.matches("\\d{8}"))
            return date;

        if (date.contains(".")) {
            newDate = date.replace(".", "");
        }
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        try {
            LocalDate localDate = LocalDate.parse(newDate, inputFormatter);
            return localDate.format(formatter);
        } catch (DateTimeParseException e) {
            return date;
        }
    }

    public static String convertDateddMMyyyy(String date) {
        String newDate = date;

        if (date == null || date.equals("0.00") || !date.matches("\\d{8}"))
            return date;

        if (date.contains(".")) {
            newDate = date.replace(".", "");
        }
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("ddMMyyyy");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        try {
            LocalDate localDate = LocalDate.parse(newDate, inputFormatter);
            return localDate.format(formatter);
        } catch (DateTimeParseException e) {
            return date;
        }
    }

    public static String convertDateFormatSG(String date) {
        if (date == null)
            return null;

        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy");

        try {
            LocalDate localDate = LocalDate.parse(date, inputFormatter);
            return localDate.format(formatter);
        } catch (DateTimeParseException e) {
            return date;
        }
    }

    public static String getTodayDateOvFormat() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        String formattedDate = today.format(formatter);
        return formattedDate;
    }

    public static String getTMinusDateOvFormat() {
        LocalDate tminusOne = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
        String formattedDate = tminusOne.format(formatter);
        return formattedDate;
    }

    public static String getCurrentDateTime() {
        OffsetDateTime dateTime = OffsetDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss '(GMT'X')'");
        String formattedDateTime = dateTime.format(formatter);
        return formattedDateTime;
    }

}
