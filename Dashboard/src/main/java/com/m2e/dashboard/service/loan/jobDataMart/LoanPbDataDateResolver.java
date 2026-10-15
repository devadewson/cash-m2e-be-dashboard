package com.m2e.dashboard.service.loan.jobDataMart;


import com.m2e.dashboard.repository.LoanPbFacilityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Menentukan tanggal data yang ditampilkan: tanggal muat terakhir, bukan hari ini.
 * Sebelum job hari ini jalan, dashboard menampilkan data kemarin, bukan kosong.
 */
@Component
@RequiredArgsConstructor
public class LoanPbDataDateResolver {

    private final LoanPbFacilityRepository facilityRepository;

    public record DataRange(LocalDateTime loadedAt, LocalDateTime startOfDay, LocalDateTime endOfDay) {}

    public Optional<DataRange> resolveLatest() {
        LocalDateTime latest = facilityRepository.findLatestProcessDate();
        if (latest == null) {
            return Optional.empty();
        }
        LocalDateTime startOfDay = latest.toLocalDate().atStartOfDay();
        return Optional.of(new DataRange(latest, startOfDay, startOfDay.plusDays(1)));
    }
}