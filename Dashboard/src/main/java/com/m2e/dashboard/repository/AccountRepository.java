package com.m2e.dashboard.repository;

import com.m2e.dashboard.projection.*;
import com.m2e.model.sso.base.Account;
import com.m2e.utils.redis.helper.RedisHelper;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    // can delete after mock data is removed
    @Query(value="SELECT PROVIDER FROM TBL_ACCT@CTS_SSO WHERE ACCT_NO = :accountNo",nativeQuery = true)
    Optional<String> findProviderByAccountNo(String accountNo);

    @Query(value = """
        SELECT
            COALESCE(acct_pref_nm, prod_nm) as acctName,
            prod_nm as productName,
            prod_cd as productCode,
            acct_status as acctStatusCode,
            acct_status_desc as acctStatusDesc
        FROM MV_USER_ENTITLEMENT mv
        WHERE mv.acct_no = :acctNo
            AND mv.corp_usr_id = :userId
            AND mv.corp_id = :corpId
            AND mv.provider = :provider
            AND (:cifNo is NULL OR mv.cif_no = :cifNo)
            AND mv.product = :product
            AND mv.sub_product = :subProduct
            AND mv.is_add_oth_bank_acct = 'N'
    """, nativeQuery = true)
    Optional<AcctInfoProjection> findAcctInfo(String acctNo, String userId, String corpId, String cifNo, String product, String subProduct, String provider);

    @Query(value = """
        SELECT sts.CD as acctStatusCode, sts.DSCP_01 as acctStatusDesc
            FROM TBL_ACCT@cts_sso a
            JOIN TBL_MT_ACCT_STS@cts_sso sts ON sts.id = a.acct_sts
            WHERE a.acct_no = :acctNo
    """, nativeQuery = true)
    Optional<AcctStatusProjection> findAcctSts(String acctNo);

    @Query(value = """
        SELECT NULL as acctStatusCode, STATUS as acctStatusDesc
            FROM TBL_ACCT_REF@cts_sso WHERE ACCOUNT_NUMBER = :acctNo
    """, nativeQuery = true)
    Optional<AcctStatusProjection> findFitasAcctSts(String acctNo);

    @Cacheable(RedisHelper.SYSTEM_SCOPED_REDIS_PREFIX + ":findAcctDetails")
    @Query(value = """
        SELECT a.provider AS provider,
              COALESCE(a.acct_pref_nm, hpm.host_product_dscp, a.prod_nm, a.acct_nm) AS displayName,
              a.acct_nm       AS accountName,
              a.acct_pref_nm  AS accountPrefName,
              a.acct_prod_typ AS accountProdType,
              a.acct_typ      AS accountType,
              b.dscp          AS accountTypeDesc,
              COALESCE(hpm.host_product_dscp,a.prod_nm) AS productName,
              a.prod_cd       AS productCode,
              a.is_islamic    AS isIslamic,
              a.cif_no        AS cifNo,
              a.CURRENCY      AS currency,
              sts.dscp_01     AS accountStatusDesc
            FROM TBL_ACCT@CTS_SSO a
            INNER JOIN TBL_MT_ACCT_TYP@CTS_SSO b
            ON a.acct_typ = b.cd
            LEFT JOIN TBL_MT_ACCT_STS@CTS_SSO sts
            ON sts.id       = a.acct_sts
            LEFT JOIN tbl_mt_host_prod_matrix@cts_sso hpm
            on a.prod_cd = hpm.host_product_cd and a.provider = hpm.provider
            WHERE a.ACCT_NO = :acctNo
            AND a.CURRENCY  = :currency
            AND a.IS_DEL = 'N'
            AND a.IS_DEACTIVATED = 'N'
    """, nativeQuery = true)
    Optional<DepositAcctDetailPageProjection> findAcctDetails(String acctNo, String currency);

    @Query(value = """
        SELECT DISTINCT a.provider AS provider,
          COALESCE(a.acct_pref_nm, hpm.host_product_dscp, a.prod_nm, a.acct_nm) AS displayName,
          a.acct_nm       AS accountName,
          a.acct_pref_nm  AS accountPrefName,
          a.acct_prod_typ AS accountProdType,
          a.acct_typ      AS accountType,
          b.dscp          AS accountTypeDesc,
          COALESCE(hpm.host_product_dscp,a.prod_nm) AS productName,
          a.prod_cd       AS productCode,
          a.is_islamic    AS isIslamic,
          a.cif_no        AS cifNo,
          a.CURRENCY      AS currency,
          sts.dscp_01     AS accountStatusDesc
          FROM tbl_corp@cts_sso c
        INNER JOIN tbl_corp_usr_group@cts_sso cug ON c.id = cug.corp_id
        INNER JOIN tbl_corp_usr_group_usr@cts_sso cugu ON cug.id = cugu.corp_usr_group_id
        INNER JOIN tbl_corp_usr_group_acct@cts_sso cuga ON cugu.corp_usr_group_id = cuga.corp_usr_group_id
        INNER JOIN tbl_corp_accts@cts_sso ca ON cuga.corp_acct_id = ca.id
        LEFT JOIN tbl_acct@cts_sso a ON ca.acct_id = a.id
        INNER JOIN TBL_MT_ACCT_TYP@CTS_SSO b
        ON a.acct_typ = b.cd
        LEFT JOIN TBL_MT_ACCT_STS@CTS_SSO sts
        ON sts.id       = a.acct_sts
        LEFT JOIN tbl_mt_host_prod_matrix@cts_sso hpm
        on a.prod_cd = hpm.host_product_cd and a.provider = hpm.provider
        WHERE a.ACCT_NO = :acctNo
        AND a.CURRENCY  = :currency
        AND cugu.corp_usr_id = :userId
        AND c.corp_id = :corpId
        AND a.IS_DEL = 'N'
        AND a.IS_DEACTIVATED = 'N'
        AND c.is_del = 'N'
        AND c.is_deactivated = 'N'
        AND a.is_add_oth_bank_acct = 'N'
    """, nativeQuery = true)
    Optional<DepositAcctDetailPageProjection> findAcctDetails(String acctNo, String currency, String userId, String corpId);

    @Query(value="SELECT a.provider AS provider, COALESCE(a.ACCT_PREF_NM, a.ACCT_NM) AS accountName, a.acct_pref_nm AS acctPrefNm, a.prod_nm AS productName FROM TBL_ACCT@CTS_SSO a WHERE a.ACCT_NO = ?1 AND a.CURRENCY = ?2",nativeQuery = true)
    Optional<AcctDetailPageProjection> findAccountDetails(String acctNo, String currency);

    @Query(value="SELECT a.provider AS provider, a.acct_nm AS accountName, a.acct_typ AS accountType, b.dscp as accountTypeDesc, a.prod_nm as productName, a.prod_cd as productCode, a.is_islamic as isIslamic, a.cif_no as cifNo, a.currency as currency FROM TBL_ACCT@CTS_SSO a INNER JOIN TBL_MT_ACCT_TYP@CTS_SSO b ON a.acct_typ = b.cd " +
            " WHERE a.ACCT_NO = ?1 AND a.provider = ?2",nativeQuery = true)
    Optional<DepositAcctDetailPageProjection> findAcctDetailByAcctNumberAndProvider(String acctNo, String provider);

    @Query(value="""
            WITH acct_base AS (
              SELECT DISTINCT
                a.acct_no,
                a.cif_no,
                a.acct_typ,
                a.acct_pref_nm,
                a.prod_nm,
                a.prod_cd,
                a.provider,
                hpm.host_product_dscp,
                apm.product_desc,
                CASE
                  WHEN a.provider = 'SG' AND apm.product_desc IS NOT NULL THEN apm.product_desc
                  WHEN a.provider = 'MY' AND hpm.host_product_dscp IS NOT NULL THEN hpm.host_product_dscp
                  ELSE COALESCE(a.prod_nm, a.prod_cd)
                END AS defaultName
              FROM tbl_acct@cts_sso a
              LEFT JOIN tbl_mt_host_prod_matrix@cts_sso hpm
                ON a.prod_cd = hpm.host_product_cd
              LEFT JOIN tbl_mt_acct_typ_matrix@cts_sso atm
                ON a.acct_typ = atm.account_type_cd AND a.provider = atm.provider
              LEFT JOIN tbl_mt_acct_prod_matrix_sg@cts_sso apm
                ON (
                  (a.prod_cd = apm.product_cd OR a.prod_cd = apm.product_desc)
                  AND (apm.account_type_cd IS NULL OR atm.esb_acct_typ_cd = apm.account_type_cd)
                )
            )
            SELECT
              acct_no AS acctNumber,
              COALESCE(acct_pref_nm, defaultName) AS acctName,
              defaultName,
              a.acct_typ AS acctTyp,
              a.cif_no AS cifNumber
            FROM acct_base a
            WHERE acct_no = :acctNumber
            """,nativeQuery = true)
    Optional<AcctProjection> findByAcctNumber(String acctNumber);
}