package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.AccountTypeProjection;
import com.m2e.model.sso.base.AccountTypeMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountTypeRepository extends JpaRepository<AccountTypeMaintenance, String> {
    @Query(value = "SELECT t.ID AS id, t.CD AS code, t.DSCP AS description \n" +
            "FROM TBL_MT_ACCT_TYP@CTS_SSO t \n" +
            "INNER JOIN TBL_MT_ACCT_PROD_TYP@CTS_SSO p ON t.mt_acct_prod_typ_id = p.ID \n" +
            "WHERE p.CD = :productCode \n",
            nativeQuery = true)
    List<AccountTypeProjection> findAcctTypByProdCode(String productCode);

    @Query(value = "SELECT t.ID AS id, t.CD AS code, t.DSCP AS description \n" +
            "FROM TBL_MT_ACCT_TYP@CTS_SSO t \n" +
            "WHERE t.CD = :cd \n",
            nativeQuery = true)
    Optional<AccountTypeProjection> findAcctTypByCd(String cd);
}
