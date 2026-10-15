package com.m2e.dashboard.repository;

import com.m2e.dashboard.dto.loan.LoanPbAmountByCurrencyProjection;
import com.m2e.dashboard.dto.overview.CifAmountByCurrencyProjection;
import com.m2e.dashboard.model.DataMart.LoanPbDrawEntity;
import com.m2e.dashboard.projection.LoanPbDrawListingProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanPbDrawRepository extends JpaRepository<LoanPbDrawEntity, Long> {

    /** Draw produk ini milik CIF pada tanggal data. CIF selalu ikut disaring. */
    String DRAW_OF_PRODUCT = """
            FROM LoanPbDrawEntity d
            WHERE d.cifNo = :cifNo
              AND d.accrueMethod = :accrueMethod
              AND d.processDate >= :startOfDay
              AND d.processDate < :endOfDay
            """;

    /** Seluruh draw untuk fasilitas di satu halaman account list. */
    @Query("SELECT d.noteNo AS noteNo, d.drawNo AS drawNo, d.drawDate AS drawDate, "
            + "d.currency AS currency, d.outstanding AS outstanding, d.maturityDate AS maturityDate, "
            + "d.nextPaymentDue AS nextPaymentDue, d.nextPaymentDate AS nextPaymentDate "
            + DRAW_OF_PRODUCT
            + " AND d.noteNo IN :noteNos"
            + " ORDER BY d.noteNo, d.drawNo")
    List<LoanPbDrawListingProjection> findDraws(
            @Param("cifNo") String cifNo,
            @Param("accrueMethod") String accrueMethod,
            @Param("noteNos") List<String> noteNos,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    /**
     * Total outstanding seluruh draw di account list satu CIF, per currency draw.
     * Draw tanpa baris fasilitas tidak tampil di listing, jadi ikut dikecualikan.
     */
    @Query("SELECT new  com.m2e.dashboard.dto.loan.LoanPbAmountByCurrencyProjection("
            + "d.currency, COUNT(d.id), COALESCE(SUM(d.outstanding), 0)) "
            + DRAW_OF_PRODUCT
            + " AND d.noteNo IN ("
            + "     SELECT f.noteNo FROM LoanPbFacilityEntity f"
            + "     WHERE f.cifNo = :cifNo"
            + "       AND f.processDate >= :startOfDay"
            + "       AND f.processDate < :endOfDay)"
            + " GROUP BY d.currency")
    List<LoanPbAmountByCurrencyProjection> sumOutstandingByCurrency(
            @Param("cifNo") String cifNo,
            @Param("accrueMethod") String accrueMethod,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    /** Sama dengan sumOutstandingByCurrency, tapi untuk seluruh CIF sekaligus (job snapshot). */
    @Query("""
        SELECT new com.m2e.dashboard.dto.overview.CifAmountByCurrencyProjection(
            d.cifNo, d.currency, COALESCE(SUM(d.outstanding), 0))
        FROM LoanPbDrawEntity d
        WHERE d.accrueMethod = :accrueMethod
          AND d.processDate >= :startOfDay
          AND d.processDate < :endOfDay
          AND EXISTS (
              SELECT 1 FROM LoanPbFacilityEntity f
              WHERE f.cifNo = d.cifNo
                AND f.noteNo = d.noteNo
                AND f.processDate >= :startOfDay
                AND f.processDate < :endOfDay)
        GROUP BY d.cifNo, d.currency
        """)
    List<CifAmountByCurrencyProjection> sumOutstandingGroupedByCif(
            @Param("accrueMethod") String accrueMethod,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    @Modifying
    @Query("DELETE FROM LoanPbDrawEntity d "
            + "WHERE d.processDate >= :startOfDay AND d.processDate < :endOfDay")
    int deleteByProcessDateRange(@Param("startOfDay") LocalDateTime startOfDay,
                                 @Param("endOfDay") LocalDateTime endOfDay);
}