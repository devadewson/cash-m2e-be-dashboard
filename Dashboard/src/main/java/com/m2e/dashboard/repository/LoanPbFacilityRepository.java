package com.m2e.dashboard.repository;


import com.m2e.dashboard.dto.loan.LoanPbAmountByCurrencyProjection;
import com.m2e.dashboard.dto.overview.CifAmountByCurrencyProjection;
import com.m2e.dashboard.model.DataMart.LoanPbFacilityEntity;
import com.m2e.dashboard.projection.LoanPbFacilityListingProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface LoanPbFacilityRepository extends JpaRepository<LoanPbFacilityEntity, Long> {

    /**
     * Fasilitas milik CIF pada tanggal data yang punya minimal satu draw produk ini.
     * Dipakai bersama oleh listing dan stats agar keduanya selalu menghitung set yang sama.
     */
    String FACILITY_OF_PRODUCT = """
            FROM LoanPbFacilityEntity f
            WHERE f.cifNo = :cifNo
              AND f.processDate >= :startOfDay
              AND f.processDate < :endOfDay
              AND f.noteNo IN (
                  SELECT d.noteNo FROM LoanPbDrawEntity d
                  WHERE d.cifNo = :cifNo
                    AND d.accrueMethod = :accrueMethod
                    AND d.processDate >= :startOfDay
                    AND d.processDate < :endOfDay)
            """;

    /** Waktu muat terakhir. Kedua tabel dimuat dalam satu transaksi, jadi cukup satu tabel. */
    @Query("SELECT MAX(f.processDate) FROM LoanPbFacilityEntity f")
    LocalDateTime findLatestProcessDate();

    @Query(value = "SELECT f.noteNo AS noteNo, f.currency AS currency, f.plafond AS plafond, "
            + "f.availableLimit AS availableLimit, f.availablePeriod AS availablePeriod "
            + FACILITY_OF_PRODUCT
            + " ORDER BY f.noteNo",
            countQuery = "SELECT COUNT(f.id) " + FACILITY_OF_PRODUCT)
    Page<LoanPbFacilityListingProjection> getFacilityPage(
            @Param("cifNo") String cifNo,
            @Param("accrueMethod") String accrueMethod,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable);

    /** Total original limit seluruh fasilitas di account list, per currency fasilitas. */
    @Query("SELECT new com.m2e.dashboard.dto.loan.LoanPbAmountByCurrencyProjection("
            + "f.currency, COUNT(f.id), COALESCE(SUM(f.plafond), 0)) "
            + FACILITY_OF_PRODUCT
            + " GROUP BY f.currency")
    List<LoanPbAmountByCurrencyProjection> sumPlafondByCurrency(
            @Param("cifNo") String cifNo,
            @Param("accrueMethod") String accrueMethod,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay);

    @Modifying
    @Query("DELETE FROM LoanPbFacilityEntity f "
            + "WHERE f.processDate >= :startOfDay AND f.processDate < :endOfDay")
    int deleteByProcessDateRange(@Param("startOfDay") LocalDateTime startOfDay,
                                 @Param("endOfDay") LocalDateTime endOfDay);

    /** Sama dengan sumOutstandingByCurrency, tapi untuk seluruh CIF sekaligus. */
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
}
