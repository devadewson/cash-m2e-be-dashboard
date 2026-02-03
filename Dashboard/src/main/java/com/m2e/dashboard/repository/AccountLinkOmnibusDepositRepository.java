package com.m2e.dashboard.repository;

import com.m2e.model.sso.base.AccountLinkOmnibusDeposit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountLinkOmnibusDepositRepository extends JpaRepository<AccountLinkOmnibusDeposit, String> {
    @Query(value = "select * from TBL_ACCT_LINK_OMNIBUS_DP@CTS_SSO where CHILD_ACCOUNT_NO = :childAccountNo", nativeQuery = true)
    Optional<AccountLinkOmnibusDeposit> findByChildAccountNo(@Param("childAccountNo") String childAccountNo);

}
