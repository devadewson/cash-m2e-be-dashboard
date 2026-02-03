create or replace NONEDITIONABLE PROCEDURE VA_ACCT_LIST
(
    searchParam     IN VARCHAR2,
    virtualAcctNo   IN VARCHAR2,
    corporateCode   IN VARCHAR2,
    corpId          IN VARCHAR2,
    userId          IN VARCHAR2,
    pageNumber      IN NUMBER,
    pageSize        IN NUMBER,
    CURSOR          OUT SYS_REFCURSOR
) AS
    v_start_index   NUMBER;
    v_end_index     NUMBER;

BEGIN
    -- Calculate pagination index
    v_start_index := (pageNumber - 1) * pageSize + 1;
    v_end_index := pageNumber * pageSize;

    -- Use the collection in the second query with pagination directly in the WITH clause
    OPEN CURSOR FOR
      WITH acct_list AS (
        SELECT * FROM (
          SELECT
            COUNT(*) OVER () AS totalCount,
            mv.ACCT_NO,
            ROW_NUMBER() OVER (ORDER BY mv.ACCT_NO) AS rn  -- Assign row numbers for pagination
          FROM TBL_ACCT_VA@CTS_SSO ut
          INNER JOIN MV_USER_ENTITLEMENT mv ON ut.PARENT_ACCOUNT = mv.ACCT_NO
          WHERE mv.CORP_USR_ID = userId
            AND mv.CORP_ID = corpId
            AND ((UPPER(mv.acct_nm) LIKE '%' || UPPER(searchParam) || '%')
              OR (NVL2(searchParam, ut.PARENT_ACCOUNT, ut.PARENT_ACCOUNT) LIKE NVL2(searchParam, '%'|| searchParam || '%', ut.PARENT_ACCOUNT))
              OR (NVL2(searchParam, ut.VIRTUAL_ACCOUNT, ut.VIRTUAL_ACCOUNT) LIKE NVL2(searchParam, '%'|| searchParam || '%', ut.VIRTUAL_ACCOUNT))
              OR (NVL2(searchParam, ut.CORP_CODE, ut.CORP_CODE) LIKE NVL2(searchParam, '%'|| searchParam || '%', ut.CORP_CODE))
              OR (NVL2(searchParam, ut.VIRTUAL_ACCOUNT_REF, ut.VIRTUAL_ACCOUNT_REF) LIKE NVL2(searchParam, '%'|| searchParam || '%', ut.VIRTUAL_ACCOUNT_REF)))
            AND NVL2(virtualAcctNo, ut.VIRTUAL_ACCOUNT, ut.VIRTUAL_ACCOUNT) LIKE NVL2(virtualAcctNo, '%'|| virtualAcctNo || '%', ut.VIRTUAL_ACCOUNT)
            AND NVL2(corporateCode, ut.CORP_CODE, ut.CORP_CODE) LIKE NVL2(corporateCode, '%'|| corporateCode || '%', ut.CORP_CODE)
          GROUP BY mv.ACCT_NO
        )
        WHERE rn BETWEEN v_start_index AND v_end_index
      )
      SELECT
        al.totalCount,
        mv.ACCT_NM AS currentAccName,
        mv.ACCT_NO AS currentAccNo,
        COUNT(ut.VIRTUAL_ACCOUNT) AS totVa,
        ut.CORP_CODE AS corpCode,
        ut.VIRTUAL_ACCOUNT AS virtualAccount,
        ut.VIRTUAL_ACCOUNT_NAME AS virtualAccountName,
        ut.VIRTUAL_ACCOUNT_REF AS virtualAccountRef
      FROM TBL_ACCT_VA@CTS_SSO ut
      INNER JOIN MV_USER_ENTITLEMENT mv ON ut.PARENT_ACCOUNT = mv.ACCT_NO
      INNER JOIN acct_list al ON mv.ACCT_NO = al.ACCT_NO
      WHERE
        mv.CORP_USR_ID = userId
        AND mv.CORP_ID = corpId
        AND ((UPPER(mv.acct_nm) LIKE '%' || UPPER(searchParam) || '%')
          OR (NVL2(searchParam, ut.PARENT_ACCOUNT, ut.PARENT_ACCOUNT) LIKE NVL2(searchParam, '%' || searchParam || '%', ut.PARENT_ACCOUNT))
          OR (NVL2(searchParam, ut.VIRTUAL_ACCOUNT, ut.VIRTUAL_ACCOUNT) LIKE NVL2(searchParam, '%' || searchParam || '%', ut.VIRTUAL_ACCOUNT))
          OR (NVL2(searchParam, ut.CORP_CODE, ut.CORP_CODE) LIKE NVL2(searchParam, '%' || searchParam || '%', ut.CORP_CODE))
          OR (NVL2(searchParam, ut.VIRTUAL_ACCOUNT_REF, ut.VIRTUAL_ACCOUNT_REF) LIKE NVL2(searchParam, '%' || searchParam || '%', ut.VIRTUAL_ACCOUNT_REF)))
        AND NVL2(virtualAcctNo, ut.VIRTUAL_ACCOUNT, ut.VIRTUAL_ACCOUNT) LIKE NVL2(virtualAcctNo, '%'|| virtualAcctNo || '%', ut.VIRTUAL_ACCOUNT)
        AND NVL2(corporateCode, ut.CORP_CODE, ut.CORP_CODE) LIKE NVL2(corporateCode, '%'|| corporateCode || '%', ut.CORP_CODE)
      GROUP BY mv.ACCT_NM, mv.ACCT_NO, ut.CORP_CODE,  ut.VIRTUAL_ACCOUNT, ut.VIRTUAL_ACCOUNT_NAME, ut.VIRTUAL_ACCOUNT_REF, al.totalCount;

END VA_ACCT_LIST;