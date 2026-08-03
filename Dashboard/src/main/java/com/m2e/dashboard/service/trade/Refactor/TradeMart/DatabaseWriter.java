package com.m2e.dashboard.service.trade.Refactor.TradeMart;

import com.m2e.dashboard.model.DataMart.DatamartAgreement;
import com.m2e.dashboard.repository.DatamartAgreementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class DatabaseWriter {

    private final DatamartAgreementRepository repository;

    @Transactional
    public void save(List<DatamartAgreement> records) {
        try {
            repository.saveAll(records);
            log.debug("[DatabaseWriter] Inserted {} records", records.size());
        } catch (Exception e) {
            log.error("[DatabaseWriter] Failed to insert {} records. First record REF_NO={}, error={}",
                    records.size(),
                    records.isEmpty() ? "N/A" : records.get(0).getRefNo(),
                    e.getMessage(), e);
            throw e;
        }
    }
}