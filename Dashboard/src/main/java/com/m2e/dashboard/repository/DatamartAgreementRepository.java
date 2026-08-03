package com.m2e.dashboard.repository;

import com.m2e.dashboard.dto.trade.DatamartListingProjection;
import com.m2e.dashboard.dto.trade.TradeAcctStatsProjection;
import com.m2e.dashboard.model.DataMart.DatamartAgreement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;

public interface DatamartAgreementRepository extends JpaRepository<DatamartAgreement, Long> {

    @Query("""
        SELECT new com.m2e.dashboard.dto.trade.TradeAcctStatsProjection(
            COUNT(d.id),
            COALESCE(SUM(d.outstandingAmount), 0)
        )
        FROM DatamartAgreement d
        WHERE d.facility = :corpId
          AND d.productType = :acctType
          AND d.processDate >= :startOfDay
          AND d.processDate < :endOfDay
        """)
    TradeAcctStatsProjection getAccountStats(
            @Param("corpId") String corpId,
            @Param("acctType") String acctType,
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
        WHERE d.facility = :corpId
          AND d.productType = :acctType
          AND d.processDate >= :startOfDay
          AND d.processDate < :endOfDay
        """)
    Page<DatamartListingProjection> getAccountListing(
            @Param("corpId") String corpId,
            @Param("acctType") String acctType,
            @Param("startOfDay") LocalDateTime startOfDay,
            @Param("endOfDay") LocalDateTime endOfDay,
            Pageable pageable
    );
}