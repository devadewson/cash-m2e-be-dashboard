package com.m2e.dashboard.service.overview.AssetGraph;


import com.m2e.dashboard.model.Overview.AssetSnapshotEntity;
import com.m2e.dashboard.repository.AssetSnapshotRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AssetSnapshotWriter {

    private final AssetSnapshotRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void upsert(AssetSnapshotEntity entity) {
        repository.deleteByCifAndDate(entity.getCorpCifNo(), entity.getSnapshotDate());
        repository.flush();
        repository.save(entity);
    }
}