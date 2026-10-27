package com.m2e.dashboard.repository;

import com.m2e.dashboard.model.DataMart.LoanPlafondEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface LoanPlafondRepository extends JpaRepository<LoanPlafondEntity, Long> {

    @Query("SELECT MAX(p.processDate) FROM LoanPlafondEntity p")
    LocalDateTime findLatestProcessDate();

    @Query("""
        SELECT p.plafond
        FROM   LoanPlafondEntity p
        WHERE  p.cifNo = :cifNo
          AND  p.processDate >= :startOfDay
          AND  p.processDate <  :endOfDay
        ORDER  BY p.id DESC
        """)
    List<BigDecimal> findPlafonds(@Param("cifNo") String cifNo,
                                  @Param("startOfDay") LocalDateTime startOfDay,
                                  @Param("endOfDay") LocalDateTime endOfDay);

    @Modifying
    @Query("DELETE FROM LoanPlafondEntity p WHERE p.processDate >= :startOfDay AND p.processDate < :endOfDay")
    int deleteByProcessDateRange(@Param("startOfDay") LocalDateTime startOfDay,
                                 @Param("endOfDay") LocalDateTime endOfDay);
}
