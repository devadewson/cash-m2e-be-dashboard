package com.m2e.dashboard.service.overview.Datamart;


import com.m2e.dashboard.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Tanggal data yang ditampilkan untuk tiap sumber datamart: tanggal muat terakhir, bukan hari ini.
 * Sebelum job hari ini jalan, endpoint menampilkan data kemarin, bukan kosong.
 */
@Component
@RequiredArgsConstructor
public class DatamartDateResolver {

    private final DatamartAgreementRepository tradeRepository;
    private final DepositFdRepository fdRepository;
    private final LoanPrkRepository prkRepository;
    private final LoanPbFacilityRepository pbFacilityRepository;

    private final LoanPlafondRepository plafondRepository;

    public Optional<DatamartDateRange> trade() {
        return DatamartDateRange.fromLatest(tradeRepository.findLatestProcessDate());
    }

    public Optional<DatamartDateRange> fd() {
        return DatamartDateRange.fromLatest(fdRepository.findLatestProcessDate());
    }

    public Optional<DatamartDateRange> prk() {
        return DatamartDateRange.fromLatest(prkRepository.findLatestProcessDate());
    }

    public Optional<DatamartDateRange> pb() {
        return DatamartDateRange.fromLatest(pbFacilityRepository.findLatestProcessDate());
    }
    public Optional<DatamartDateRange> plafond() {
        return DatamartDateRange.fromLatest(plafondRepository.findLatestProcessDate());
    }
}
