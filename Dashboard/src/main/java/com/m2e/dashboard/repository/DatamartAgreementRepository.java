package com.m2e.dashboard.repository;

import com.m2e.dashboard.dto.trade.TradeAcctStatsByCurrencyProjection;
import com.m2e.dashboard.dto.trade.TradeOutstandingByCifProjection;
import com.m2e.dashboard.dto.trade.TradeOutstandingByProductProjection;
import com.m2e.dashboard.model.DataMart.DatamartAgreement;
import com.m2e.dashboard.projection.DatamartListingProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface DatamartAgreementRepository extends JpaRepository<DatamartAgreement, Long> {

    /**
     * Statistik per currency. Menerima beberapa productType sekaligus karena
     * satu produk bisa mencakup tahap issuance dan acceptance.
     */
    @Query("""
        SELECT new com.m2e.dashboard.dto.trade.TradeAcctStatsByCurrencyProjection(
            d.currency,
            COUNT(d.id),
            COALESCE(SUM(d.outstandingAmount), 0)
        )
        FROM DatamartAgreement d
        WHERE d.facility = :cifNo
          AND d.productType IN :acctTypes
          AND d.processDate >= :startOfDay
          AND d.processDate < :endOfDay
        GROUP BY d.currency
        """)
    List<TradeAcctStatsByCurrencyProjection> getAccountStats(
            @Param("cifNo") String cifNo,
            @Param("acctTypes") List<String> acctTypes,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    @Query("""
        SELECT d.refNo AS refNo,
               d.productType AS productType,
               d.currency AS currency,
               d.initialAmount AS initialAmount,
               d.outstandingAmount AS outstandingAmount,
               d.tenor AS tenor,
               d.releaseDate AS releaseDate,
               d.maturityDate AS maturityDate,
               d.drawer AS drawer,
               d.remitingBank AS remitingBank,
               d.beneficiary AS beneficiary,
               d.applicant AS applicant,
               d.issuingBank AS issuingBank
        FROM DatamartAgreement d
        WHERE d.facility = :cifNo
          AND d.productType = :acctType
          AND d.processDate >= :startOfDay
          AND d.processDate < :endOfDay
        """)
    Page<DatamartListingProjection> getAccountListing(
            @Param("cifNo") String cifNo,
            @Param("acctType") String acctType,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable
    );

    /** Seluruh produk trade untuk satu CIF, dikelompokkan per produk dan currency. */
    @Query("""
        SELECT new com.m2e.dashboard.dto.trade.TradeOutstandingByProductProjection(
            d.productType,
            d.currency,
            COUNT(d.id),
            COALESCE(SUM(d.outstandingAmount), 0)
        )
        FROM DatamartAgreement d
        WHERE d.facility = :cifNo
          AND d.processDate >= :startOfDay
          AND d.processDate < :endOfDay
        GROUP BY d.productType, d.currency
        """)
    List<TradeOutstandingByProductProjection> getAllOutstandingByProductAndCurrency(
            @Param("cifNo") String cifNo,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );

    /** Outstanding seluruh CIF untuk satu tanggal, dipakai job snapshot. */
    @Query("""
        SELECT new com.m2e.dashboard.dto.trade.TradeOutstandingByCifProjection(
            d.facility,
            d.currency,
            COALESCE(SUM(d.outstandingAmount), 0)
        )
        FROM DatamartAgreement d
        WHERE d.processDate >= :startOfDay
          AND d.processDate < :endOfDay
          AND d.facility IS NOT NULL
        GROUP BY d.facility, d.currency
        """)
    List<TradeOutstandingByCifProjection> getAllOutstandingGroupedByCif(
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay
    );
    @Query("SELECT MAX(d.processDate) FROM DatamartAgreement d")
    LocalDateTime findLatestProcessDate();
}