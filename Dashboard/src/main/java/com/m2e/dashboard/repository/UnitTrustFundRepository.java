package com.m2e.dashboard.repository;

import com.m2e.cts.PortfolioUnitTrust;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UnitTrustFundRepository extends JpaRepository<PortfolioUnitTrust, String> {
    @Query(value = "SELECT * FROM TBL_PORTFOLIO_UT WHERE account_No = :accNo", nativeQuery = true)
    List<PortfolioUnitTrust> findByAccNo(@Param("accNo") String accNo);

    @Query(value = "SELECT * FROM TBL_PORTFOLIO_UT WHERE id = :id", nativeQuery = true)
    Optional<PortfolioUnitTrust> findByFundId(@Param("id") String id);

    @Query(value = "SELECT * FROM TBL_PORTFOLIO_UT WHERE ACCOUNT_NO = :accNo " +
            " and ( " +
            " (UPPER(FUND_NAME) LIKE '%' || UPPER(:searchParam) || '%') " +
            "or (UPPER(currency) LIKE '%' || UPPER(:searchParam) || '%')) " +
            " ORDER BY FUND_NAME ASC",
            nativeQuery = true)
    Page<PortfolioUnitTrust> findByAccNoAndSearhParam(@Param("accNo") String accNo, @Param("searchParam") String searchParam, Pageable pageable);
}
