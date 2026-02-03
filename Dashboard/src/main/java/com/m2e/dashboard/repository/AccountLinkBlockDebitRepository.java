package com.m2e.dashboard.repository;

import com.m2e.model.sso.base.AccountLinkBdDb;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountLinkBlockDebitRepository extends JpaRepository<AccountLinkBdDb, String> {
    @Query(value = "select * from TBL_ACCT_LINK_BD_DB@CTS_SSO where BLOCK_NO = :blockNo and STATUS = 'ACTIVE' ORDER BY BLOCK_NO FETCH FIRST 1 ROW ONLY", nativeQuery = true)
    Optional<AccountLinkBdDb> findByBlockNo(@Param("blockNo") String blockNo);
}
