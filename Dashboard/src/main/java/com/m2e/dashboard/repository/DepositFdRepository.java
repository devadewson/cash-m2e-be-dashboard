package com.m2e.dashboard.repository;


import com.m2e.dashboard.dto.deposit.DepositFdStatsByCurrencyProjection;
import com.m2e.dashboard.dto.overview.CifAmountByCurrencyProjection;
import com.m2e.dashboard.model.DataMart.DepositFdEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DepositFdRepository extends JpaRepository<DepositFdEntity, Long> {

    @Query("""
        SELECT new com.m2e.dashboard.dto.deposit.DepositFdStatsByCurrencyProjection(
            d.currency,
            COUNT(d.id),
            COALESCE(SUM(d.principal), 0)
        )
        FROM DepositFdEntity d
        WHERE d.cifNo = :cifNo
          AND d.processDate >= :startOfDay
          AND d.processDate < :endOfDay
        GROUP BY d.currency
        """)
    List<DepositFdStatsByCurrencyProjection> getStatsByCurrency(
            @Param("cifNo") String cifNo,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    @Query("""
        SELECT d.depositNumber   AS depositNumber,
               d.placementDate   AS placementDate,
               d.maturityDate    AS maturityDate,
               d.currency        AS currency,
               d.principal       AS principal,
               d.tenor           AS tenor,
               d.interestRate    AS interestRate
        FROM DepositFdEntity d
        WHERE d.cifNo = :cifNo
          AND d.processDate >= :startOfDay
          AND d.processDate < :endOfDay
        """)
    Page<DepositFdListingProjection> getListing(
            @Param("cifNo") String cifNo,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable);

    @Modifying
    @Query("DELETE FROM DepositFdEntity d WHERE d.processDate >= :startOfDay AND d.processDate < :endOfDay")
    int deleteByProcessDateRange(@Param("startOfDay") LocalDateTime startOfDay,
                                 @Param("endOfDay") LocalDateTime endOfDay);

    @Query("SELECT MAX(d.processDate) FROM DepositFdEntity d")
    LocalDateTime findLatestProcessDate();

    @Query("""
        SELECT new com.m2e.dashboard.dto.overview.CifAmountByCurrencyProjection(
            d.cifNo, d.currency, COALESCE(SUM(d.principal), 0))
        FROM DepositFdEntity d
        WHERE d.processDate >= :startOfDay
          AND d.processDate < :endOfDay
        GROUP BY d.cifNo, d.currency
        """)
    List<CifAmountByCurrencyProjection> sumPrincipalGroupedByCif(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);
}