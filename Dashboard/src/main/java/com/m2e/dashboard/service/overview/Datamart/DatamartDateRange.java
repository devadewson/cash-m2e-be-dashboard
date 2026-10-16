package com.m2e.dashboard.service.overview.Datamart;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/** Rentang satu hari data datamart, berdasarkan waktu muat terakhir. */
public record DatamartDateRange(LocalDateTime loadedAt, LocalDateTime startOfDay, LocalDateTime endOfDay) {

    public static Optional<DatamartDateRange> fromLatest(LocalDateTime latest) {
        if (latest == null) {
            return Optional.empty();
        }
        LocalDateTime start = latest.toLocalDate().atStartOfDay();
        return Optional.of(new DatamartDateRange(latest, start, start.plusDays(1)));
    }

    public LocalDate dataDate() {
        return startOfDay.toLocalDate();
    }
}
