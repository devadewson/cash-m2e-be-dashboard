package com.m2e.dashboard.repository;

import com.m2e.cts.PortfolioActivityUnitTrust;
import com.m2e.cts.PortfolioUnitTrust;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PortfolioActivityUTRepository extends JpaRepository<PortfolioActivityUnitTrust, String>, JpaSpecificationExecutor<PortfolioActivityUnitTrust> {
    List<PortfolioActivityUnitTrust> findByAccountNo(String accNo);

}
