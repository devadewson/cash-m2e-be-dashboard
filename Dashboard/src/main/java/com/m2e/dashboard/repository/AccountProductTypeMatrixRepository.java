package com.m2e.dashboard.repository;

import com.m2e.model.sso.base.AccountProductTypeMatrix;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountProductTypeMatrixRepository extends JpaRepository<AccountProductTypeMatrix, String> {
    @Query(value = "select * from TBL_MT_ACCT_TYP_MATRIX@CTS_SSO where ACCOUNT_TYPE_CD = :code AND provider = :provider", nativeQuery = true)
    Optional<AccountProductTypeMatrix> findByAcctTypeAndProv(@Param("code") String code, @Param("provider") String provider);

}
