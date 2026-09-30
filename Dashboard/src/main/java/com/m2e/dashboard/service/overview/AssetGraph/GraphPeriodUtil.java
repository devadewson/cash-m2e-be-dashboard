package com.m2e.dashboard.service.overview.AssetGraph;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.IsoFields;
import java.util.ArrayList;
import java.util.List;

/**
 * Perhitungan key dan label periode untuk snapshot dan grafik.
 *
 * Minggu memakai standar ISO-8601: Senin sampai Minggu, dan tahun dihitung
 * dengan WEEK_BASED_YEAR agar pergantian tahun tidak menghasilkan key salah.
 */
public final class GraphPeriodUtil {

    private GraphPeriodUtil() {}

    private static final DateTimeFormatter LABEL_DATE =
            DateTimeFormatter.ofPattern("dd-MM-yyyy");

    // ==================== KEY DARI SATU TANGGAL ====================

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

    // ==================== RENTANG UNTUK GRAFIK ====================

    /**
     * Key minggu untuk n minggu yang berakhir di endDate, urut lama ke baru.
     * Anchor di endDate, bukan hari ini, supaya jumlahnya selalu n.
     */
    public static List<String> weekKeysEndingAt(LocalDate endDate, int count) {
        List<String> keys = new ArrayList<>();
        for (int i = count - 1; i >= 0; i--) {
            keys.add(weekKey(endDate.minusWeeks(i)));
        }
        return keys;
    }

    /** Senin dari minggu paling lama dalam rentang n minggu yang berakhir di endDate. */
    public static LocalDate firstMondayEndingAt(LocalDate endDate, int weekCount) {
        return endDate.minusWeeks(weekCount - 1L).with(DayOfWeek.MONDAY);
    }

    /** Januari sampai bulan dari endDate, dalam tahun endDate. */
    public static List<String> monthKeysYearToDate(LocalDate endDate) {
        List<String> keys = new ArrayList<>();
        for (int m = 1; m <= endDate.getMonthValue(); m++) {
            keys.add(String.format("%d-%02d", endDate.getYear(), m));
        }
        return keys;
    }

    /** Q1 sampai kuartal dari endDate, dalam tahun endDate. */
    public static List<String> quarterKeysYearToDate(LocalDate endDate) {
        int lastQuarter = endDate.get(IsoFields.QUARTER_OF_YEAR);
        List<String> keys = new ArrayList<>();
        for (int q = 1; q <= lastQuarter; q++) {
            keys.add(String.format("%d-Q%d", endDate.getYear(), q));
        }
        return keys;
    }

    /** 1 Januari tahun dari endDate. */
    public static LocalDate firstDayOfYear(LocalDate endDate) {
        return endDate.withDayOfYear(1);
    }

    // ==================== LABEL ====================

    /**
     * Label rentang minggu untuk grafik, dipotong di endDate bila minggu belum selesai.
     *
     * Minggu penuh    : "21-09-2026 - 27-09-2026"
     * Minggu berjalan : "28-09-2026 - 29-09-2026"
     */
    public static String weekRangeLabel(LocalDate anyDayInWeek, LocalDate endDate) {
        LocalDate start = anyDayInWeek.with(DayOfWeek.MONDAY);
        LocalDate end = anyDayInWeek.with(DayOfWeek.SUNDAY);

        if (end.isAfter(endDate)) {
            end = endDate;
        }

        return start.format(LABEL_DATE) + " - " + end.format(LABEL_DATE);
    }
}