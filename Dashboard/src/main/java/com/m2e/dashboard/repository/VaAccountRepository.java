package com.m2e.dashboard.repository;

import com.m2e.dashboard.dto.deposit.va.response.DepositCADropdownDTO;
import com.m2e.model.sso.base.PortfolioVirtualAccount;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VaAccountRepository extends JpaRepository<PortfolioVirtualAccount, String> {

    @Query(value = "SELECT * FROM ( " +
            "SELECT ID, "+
            "CORP_CODE, "+
            "PARENT_ACCOUNT, "+
            "PARENT_ACCOUNT_CURRENCY, "+
            "PARENT_ACCOUNT_NAME, "+
            "PARENT_ACCOUNT_CIF, "+
            "PARENT_ACCOUNT_ID, "+
            "VIRTUAL_ACCOUNT, "+
            "VIRTUAL_ACCOUNT_CURRENCY, "+
            "VIRTUAL_ACCOUNT_NAME, "+
            "VIRTUAL_ACCOUNT_REF, "+
            "VIRTUAL_ACCOUNT_STATUS, "+
            "VIRTUAL_ACCOUNT_ID, "+
            "ALLOW_DUPLICATE_IND, "+
            "ID_CREATED, "+
            "ID_UPDATED, "+
            "ID_CLOSED, "+
            "VA_AS_REF_NO, "+
            "BATCH_SUMMARY_ID, "+
            "SYS_PVD_CD, "+
            "DT_CREATED, "+
            "TS_CREATED, "+
            "DT_UPDATED, "+
            "TS_UPDATED, "+
            "VERSION, "+
            "DATE_CLOSED, "+
            "DATE_CREATED, "+
            "DATE_UPDATED,  "+
            "ROW_NUMBER() OVER (PARTITION BY VIRTUAL_ACCOUNT ORDER BY dt_Created DESC) AS rn " +
            "FROM TBL_ACCT_VA@CTS_SSO  " +
            "WHERE parent_account = :accNo ) "+
            " where rn = 1 ", nativeQuery = true)
    List<PortfolioVirtualAccount> findByParentAcc(@Param("accNo") String parentAccNo);

    @Query(value = "SELECT * FROM TBL_ACCT_VA@CTS_SSO WHERE VIRTUAL_ACCOUNT = :vaAccNo " +
            " order by dt_created desc FETCH FIRST 1 ROWS ONLY ", nativeQuery = true)
    Optional<PortfolioVirtualAccount> findByVaAccNo(@Param("vaAccNo") String vaAccNo);

    @Query(value="SELECT * FROM ( " +
            "SELECT  " +
            "ut.ID, " +
            "ut.CORP_CODE, " +
            "ut.PARENT_ACCOUNT, " +
            "ut.PARENT_ACCOUNT_CURRENCY, " +
            "ut.PARENT_ACCOUNT_NAME, " +
            "ut.PARENT_ACCOUNT_CIF, " +
            "ut.PARENT_ACCOUNT_ID, " +
            "ut.VIRTUAL_ACCOUNT, " +
            "ut.VIRTUAL_ACCOUNT_CURRENCY, " +
            "ut.VIRTUAL_ACCOUNT_NAME, " +
            "ut.VIRTUAL_ACCOUNT_REF, " +
            "ut.VIRTUAL_ACCOUNT_STATUS, " +
            "ut.VIRTUAL_ACCOUNT_ID, " +
            "ut.ALLOW_DUPLICATE_IND, " +
            "ut.ID_CREATED, " +
            "ut.ID_UPDATED, " +
            "ut.ID_CLOSED, " +
            "ut.VA_AS_REF_NO, " +
            "ut.BATCH_SUMMARY_ID, " +
            "ut.SYS_PVD_CD, " +
            "ut.DT_CREATED, " +
            "ut.TS_CREATED, " +
            "ut.DT_UPDATED, " +
            "ut.TS_UPDATED, " +
            "ut.VERSION, " +
            "ut.DATE_CLOSED, " +
            "ut.DATE_CREATED, " +
            "ut.DATE_UPDATED, " +
            "ROW_NUMBER() OVER (PARTITION BY ut.VIRTUAL_ACCOUNT ORDER BY ut.dt_Created DESC) AS rn " +
            "FROM TBL_ACCT_VA@CTS_SSO ut  " +
            "inner join MV_USER_ENTITLEMENT_VA mv on ut.virtual_account = mv.ACCT_NO  " +
            "INNER JOIN TBL_ACCT@CTS_SSO ta on ta.acct_no = ut.parent_account " +
            "where mv.corp_id = :corpId and mv.corp_usr_id = :corpUserId " +
            "AND mv.product = 'VIRTUALACCOUNT' AND mv.sub_product IN ('VA')) " +
            "where rn = 1 ", nativeQuery = true)
    List<PortfolioVirtualAccount> findByCorpId(@Param("corpId") String corpId, @Param("corpUserId") String corpUserId);

    @Query(value="SELECT mv.acct_no as acctNumber, mv.acct_nm as acctName, mv.currency " +
            "FROM MV_USER_ENTITLEMENT mv " +
            "where mv.corp_id = :corpId " +
            "AND mv.corp_usr_id = :corpUserId " +
            "AND mv.product = 'DEPOSIT' " +
            "AND mv.is_add_oth_bank_acct = 'N' " +
            "AND mv.sub_product IN ('CA', 'FCA', 'MFCA') ", nativeQuery = true)
    List<DepositCADropdownDTO> findCaByCorpAndUserId(@Param("corpId") String corpId, @Param("corpUserId") String corpUserId);

}
