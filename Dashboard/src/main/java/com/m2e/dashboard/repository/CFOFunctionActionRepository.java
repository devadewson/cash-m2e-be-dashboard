package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.CFOFunctionActionProjection;
import com.m2e.model.sso.base.CFOFunctionAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CFOFunctionActionRepository extends JpaRepository<CFOFunctionAction, String> {

    @Query(value = "select ID AS id,CD AS code,DSCP AS description FROM TBL_CFO_FUNCS_ACTION@CTS_SSO WHERE CD = :cd", nativeQuery = true)
    Optional<CFOFunctionActionProjection> findbyCode(String cd);

    @Query(value = "select ID AS id,CD AS code,DSCP AS description FROM TBL_CFO_FUNCS_ACTION@CTS_SSO WHERE id = :id", nativeQuery = true)
    Optional<CFOFunctionActionProjection> findActionById(String id);
}
