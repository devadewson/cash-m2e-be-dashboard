package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.LoanPrkListingProjection;
import com.m2e.dashboard.dto.loan.overdraft.LoanPrkStatsByCurrencyProjection;
import com.m2e.dashboard.dto.overview.CifAmountByCurrencyProjection;
import com.m2e.dashboard.model.DataMart.LoanPrkEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanPrkRepository extends JpaRepository<LoanPrkEntity, Long> {

    @Query("SELECT MAX(d.processDate) FROM LoanPrkEntity d")
    LocalDateTime findLatestProcessDate();

    /**
     * OUTSTANDING berasal dari CURRBAL (saldo berjalan). Saldo negatif berarti
     * plafond terpakai; saldo positif berarti dana mengendap dan tidak dihitung.
     */
    @Query("""
        SELECT com.m2e.dashboard.dto.loan.overdraft.LoanPrkStatsByCurrencyProjection(
            d.currency,
            COUNT(d.id),
            COALESCE(SUM(d.limitAmount), 0),
            COALESCE(SUM(CASE WHEN d.outstanding < 0 THEN -d.outstanding ELSE 0 END), 0),
            COALESCE(SUM(d.interestDue), 0)
        )
        FROM LoanPrkEntity d
        WHERE d.cifNo = :cifNo
          AND d.processDate >= :startOfDay
          AND d.processDate < :endOfDay
        GROUP BY d.currency
        """)
    List<LoanPrkStatsByCurrencyProjection> getStatsByCurrency(
            @Param("cifNo") String cifNo,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    @Query("""
        SELECT d.accountNo    AS accountNo,
               d.currency     AS currency,
               d.limitAmount  AS limitAmount,
               d.outstanding  AS outstanding,
               d.maturityDate AS maturityDate,
               d.interestDue  AS interestDue
        FROM LoanPrkEntity d
        WHERE d.cifNo = :cifNo
          AND d.processDate >= :startOfDay
          AND d.processDate < :endOfDay
        """)
    Page<LoanPrkListingProjection> getListing(
            @Param("cifNo") String cifNo,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable);

    /** Pemakaian plafond per CIF untuk job snapshot. Aturan sama dengan getStatsByCurrency. */
    @Query("""
        SELECT new com.m2e.dashboard.dto.overview.CifAmountByCurrencyProjection(
            d.cifNo, d.currency,
            COALESCE(SUM(CASE WHEN d.outstanding < 0 THEN -d.outstanding ELSE 0 END), 0))
        FROM LoanPrkEntity d
        WHERE d.processDate >= :startOfDay
          AND d.processDate < :endOfDay
        GROUP BY d.cifNo, d.currency
        """)
    List<CifAmountByCurrencyProjection> sumUtilisedGroupedByCif(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    @Modifying
    @Query("DELETE FROM LoanPrkEntity d WHERE d.processDate >= :startOfDay AND d.processDate < :endOfDay")
    int deleteByProcessDateRange(@Param("startOfDay") LocalDateTime startOfDay,
                                 @Param("endOfDay") LocalDateTime endOfDay);
}