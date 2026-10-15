package com.m2e.dashboard.service.overview.Datamart;


import com.m2e.dashboard.repository.DatamartAgreementRepository;
import com.m2e.dashboard.repository.DepositFdRepository;
import com.m2e.dashboard.repository.LoanPbFacilityRepository;
import com.m2e.dashboard.repository.LoanPrkRepository;
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

    public Optional<DatamartDateRange> trade() {
        return DatamartDateRange.fromLatest(tradeRepository.findLatestProcessDate());
    }

    public Optional<DatamartDateRange> fd() {
        return DatamartDateRange.fromLatest(fdRepository.findLatestProcessDate());
    }

    public Optional<DatamartDateRange> prk() {
        return DatamartDateRange.fromLatest(prkRepository.findLatestProcessDate());
    }

    /** Fasilitas dan draw PB/PPB dimuat dalam satu transaksi, jadi cukup tabel fasilitas. */
    public Optional<DatamartDateRange> pb() {
        return DatamartDateRange.fromLatest(pbFacilityRepository.findLatestProcessDate());
    }
}
