package com.m2e.dashboard.repository;

import com.m2e.model.sso.base.ErrorMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ErrorMaintenanceRepository extends JpaRepository<ErrorMaintenance,String> {
    @Query(value = "SELECT * FROM TBL_MT_ERR@CTS_SSO WHERE ERR_CAT = :errorCategory AND SYS_CD = :systemCode",
            nativeQuery = true)
    Optional<ErrorMaintenance> findByErrorCategoryAndSystemCode(String errorCategory, String systemCode);

    Optional<ErrorMaintenance> findBySystemCode(String systemCode);
}
