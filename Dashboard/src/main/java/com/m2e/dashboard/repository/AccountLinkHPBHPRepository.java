package com.m2e.dashboard.repository;

import com.m2e.model.sso.base.AccountLinkBhpHp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountLinkHPBHPRepository extends JpaRepository<AccountLinkBhpHp, String> {
    @Query(value = "select IND from TBL_ACCT_LINK_BHP_HP@CTS_SSO where HP_ACCOUNT_NO = :acctNumber", nativeQuery = true)
    Optional<String> findByAccountNo(@Param("acctNumber") String acctNumber);
}
