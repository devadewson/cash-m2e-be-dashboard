package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.CFOFunctionProjection;
import com.m2e.model.sso.base.CFOFunction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CFOFunctionRepository extends JpaRepository<CFOFunction, String> {

    @Query(value = "select ID AS id,CD AS code,DSCP AS description from TBL_CFO_FUNCS@CTS_SSO where IS_DEACTIVATED = 'N' and IS_DEL='N'",
            nativeQuery = true)
    List<CFOFunctionProjection> findMTTypes();

    @Query(value = "SELECT ID AS id,CD AS code,DSCP as description FROM TBL_CFO_FUNCS@CTS_SSO WHERE CD = :cd", nativeQuery = true)
    Optional<CFOFunctionProjection> findByCode(String cd);

    @Query(value = "SELECT ID AS id,CD AS code,DSCP as description FROM TBL_CFO_FUNCS@CTS_SSO WHERE id = :id", nativeQuery = true)
    Optional<CFOFunctionProjection> findFunctionById(String id);
}
