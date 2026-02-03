package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.AFFSCertNoAndCurrency;
import com.m2e.model.sso.base.AccountLinkFsCert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface FloorStockRepository extends JpaRepository<AccountLinkFsCert, String> {
    @Query(value="""
            SELECT COUNT(fs.CERTIFICATE_NO) FROM TBL_ACCT_LINK_FS_CERT@cts_sso fs
            WHERE fs.FLOOR_STOCK_ACCOUNT_NO = :acctNo AND fs.STATUS='ACTIVE'
        """ , nativeQuery = true)
    long countFloorStockCertsByAccountNo(String acctNo);

    @Query(value="""
            SELECT fs.FLOOR_STOCK_ACCOUNT_NAME as masterAcctName, fs.CERTIFICATE_NO as certNo, fs.CURRENCY FROM TBL_ACCT_LINK_FS_CERT@cts_sso fs
                WHERE fs.FLOOR_STOCK_ACCOUNT_NO = :acctNo
                AND (fs.CERTIFICATE_NO LIKE '%' || :searchParam || '%'
                    OR UPPER(fs.CURRENCY) LIKE '%' || UPPER(:searchParam) || '%')
        """ , nativeQuery = true)
    List<AFFSCertNoAndCurrency> findFloorStockCertsByAccountNoWithSearchParam(String acctNo, String searchParam);
}
