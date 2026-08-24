package com.m2e.dashboard.service.overview.AssetGraph;


import java.time.LocalDate;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;

public final class GraphPeriodUtil {

    private GraphPeriodUtil() {}

    public static String weekKey(LocalDate date) {
        int year = date.get(IsoFields.WEEK_BASED_YEAR);
        int week = date.get(IsoFields.WEEK_OF_WEEK_BASED_YEAR);
        return String.format("%d-W%02d", year, week);
    }

    public static String monthKey(LocalDate date) {
        return String.format("%d-%02d", date.getYear(), date.getMonthValue());
    }

    public static String quarterKey(LocalDate date) {
        return String.format("%d-Q%d", date.getYear(), date.get(IsoFields.QUARTER_OF_YEAR));
    }

    public static List<String> lastWeekKeys(int count) {
        LocalDate cursor = LocalDate.now();
        List<String> keys = new ArrayList<>();
        for (int i = count - 1; i >= 0; i--) {
            keys.add(weekKey(cursor.minusWeeks(i)));
        }
        return keys;
    }

    public static List<String> lastMonthKeys(int count) {
        LocalDate cursor = LocalDate.now();
        List<String> keys = new ArrayList<>();
        for (int i = count - 1; i >= 0; i--) {
            keys.add(monthKey(cursor.minusMonths(i)));
        }
        return keys;
    }

    public static List<String> lastQuarterKeys(int count) {
        LocalDate cursor = LocalDate.now();
        List<String> keys = new ArrayList<>();
        for (int i = count - 1; i >= 0; i--) {
            keys.add(quarterKey(cursor.minusMonths(i * 3L)));
        }
        return keys;
    }
}
