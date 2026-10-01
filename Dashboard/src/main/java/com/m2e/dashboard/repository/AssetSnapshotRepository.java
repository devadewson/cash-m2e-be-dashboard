package com.m2e.dashboard.repository;


import com.m2e.dashboard.model.Overview.AssetSnapshotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

                                           @Repository
                                           public interface AssetSnapshotRepository extends JpaRepository<AssetSnapshotEntity, Long> {

        @Query("SELECT s FROM AssetSnapshotEntity s " +
                "WHERE s.corpCifNo = :cif " +
                "AND s.snapshotDate >= :fromDate " +
                "AND s.snapshotDate <= :toDate " +
                "AND s.sourceStatus = 'SUCCESS' " +
                "ORDER BY s.snapshotDate ASC")
        List<AssetSnapshotEntity> findForGraph(@Param("cif") String cif,
                @Param("fromDate") LocalDate fromDate,
                @Param("toDate") LocalDate toDate);

    @Modifying
    @Query("DELETE FROM AssetSnapshotEntity s " +
            "WHERE s.corpCifNo = :cif AND s.snapshotDate = :snapshotDate")
    void deleteByCifAndDate(@Param("cif") String cif,
                            @Param("snapshotDate") LocalDate snapshotDate);
}