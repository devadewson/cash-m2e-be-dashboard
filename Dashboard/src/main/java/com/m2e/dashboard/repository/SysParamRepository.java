package com.m2e.dashboard.repository;

import com.m2e.model.sso.base.SystemParam;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SysParamRepository extends JpaRepository<SystemParam, String> {
    @Query(value = "SELECT dscp FROM TBL_SYS_PARAM@CTS_SSO WHERE cd = :code", nativeQuery = true)
    Optional<String> findByCd(String code);
}
