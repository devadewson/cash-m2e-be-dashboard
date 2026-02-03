package com.m2e.dashboard.repository;

import com.m2e.model.sso.base.AccountStatusMaintenance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountStatusRepository extends JpaRepository<AccountStatusMaintenance, String> {
    @Query(value = "SELECT dscp_01 from TBL_MT_ACCT_STS@CTS_SSO WHERE TO_NUMBER(CD) = :code AND SYS_PVD_CD = :provider ORDER BY DT_CREATED FETCH FIRST ROW ONLY", nativeQuery = true)
    Optional<String> findDescription01ByCodeAndProvider(int code, String provider);

    @Query(value = """
            SELECT s.DSCP_01 FROM TBL_ACCT@CTS_SSO a
                JOIN TBL_MT_ACCT_STS@CTS_SSO s ON a.ACCT_STS = s.ID
                WHERE ACCT_NO = :acctNo
        """, nativeQuery = true)
    Optional<String> findAcctStatusDescriptionByAcctNo(String acctNo);
}
