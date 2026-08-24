package com.m2e.dashboard.repository;


import com.m2e.dashboard.model.Overview.KursHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface KursHistoryRepository extends JpaRepository<KursHistoryEntity, Long> {

    /** Batch terakhir yang berhasil disimpan. */
    @Query(value =
            "SELECT BATCH_ID FROM ( " +
                    "  SELECT BATCH_ID FROM TB_KURS_HISTORY " +
                    "  GROUP BY BATCH_ID ORDER BY MAX(FETCH_TIME) DESC " +
                    ") WHERE ROWNUM = 1", nativeQuery = true)
    String findLatestBatchId();

    List<KursHistoryEntity> findByBatchId(String batchId);

    @Modifying
    @Query("DELETE FROM KursHistoryEntity k WHERE k.kursDate < :beforeDate")
    int deleteOlderThan(@Param("beforeDate") LocalDate beforeDate);
}
