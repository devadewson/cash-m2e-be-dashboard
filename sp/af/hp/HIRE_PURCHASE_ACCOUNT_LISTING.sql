create or replace PROCEDURE HIRE_PURCHASE_ACCOUNT_LISTING (
    searchParam     IN VARCHAR2,
    userId          IN VARCHAR2,
    corpId          IN VARCHAR2,
    pageNumber      IN NUMBER,
    pageSize        IN NUMBER,
    CURSOR          OUT SYS_REFCURSOR
) AS
    v_start_index NUMBER;
    v_end_index   NUMBER;
BEGIN
    -- Calculate pagination index
    v_start_index := (pageNumber - 1) * pageSize + 1;
    v_end_index := pageNumber * pageSize;

    OPEN CURSOR FOR
        SELECT * FROM (
            SELECT
                COUNT(*) OVER () AS totalCount,
                temp.acctName AS acctName,
                php.ACCOUNT_CHILD AS acctNumber,
                temp.currency AS currency,
                temp.masterAcctNumber AS masterAcctNumber,
                php.UTILISED_AMOUNT AS financedAmt,
                php.OUTSTANDING_BALANCE AS outstandingBalance,
                ROW_NUMBER() OVER (ORDER BY php.ACCOUNT_CHILD) AS row_num
            FROM
                (
                    SELECT
                        mv.acct_nm AS acctName,
                        alhp.CURRENCY AS currency,
                        alhp.BHP_ACCOUNT_NO AS masterAcctNumber,
                        alhp.HP_ACCOUNT_NO AS acctNumber,
                        mv.corp_usr_id AS corpUserId,
                        mv.corp_id AS corpId
                    FROM MV_USER_ENTITLEMENT mv
                    INNER JOIN TBL_ACCT_LINK_BHP_HP alhp ON mv.acct_no = alhp.BHP_ACCOUNT_NO
                    WHERE mv.sub_product = 'HP'
                    AND (NVL2(searchParam, mv.corp_short_nm, mv.corp_short_nm) LIKE NVL2(searchParam, '%'|| searchParam || '%', mv.corp_short_nm)
                        OR NVL2(searchParam, alhp.HP_ACCOUNT_NO, alhp.HP_ACCOUNT_NO) LIKE NVL2(searchParam, '%'|| searchParam || '%', alhp.HP_ACCOUNT_NO))
                    UNION ALL
                    SELECT
                        mv.acct_nm AS acctName,
                        alhp.CURRENCY AS currency,
                        alhp.BHP_ACCOUNT_NO AS masterAcctNumber,
                        alhp.HP_ACCOUNT_NO AS acctNumber,
                        mv.corp_usr_id AS corpUserId,
                        mv.corp_id AS corpId
                    FROM MV_USER_ENTITLEMENT mv
                    INNER JOIN TBL_ACCT_LINK_BHP_HP alhp ON mv.acct_no = alhp.HP_ACCOUNT_NO
                    WHERE mv.sub_product = 'HP'
                    AND (NVL2(searchParam, mv.corp_short_nm, mv.corp_short_nm) LIKE NVL2(searchParam, '%'|| searchParam || '%', mv.corp_short_nm)
                        OR NVL2(searchParam, alhp.HP_ACCOUNT_NO, alhp.HP_ACCOUNT_NO) LIKE NVL2(searchParam, '%'|| searchParam || '%', alhp.HP_ACCOUNT_NO))
                ) temp
                INNER JOIN TBL_PORTFOLIO_HP php ON php.ACCOUNT_CHILD = temp.acctNumber
            WHERE temp.corpUserId = userId AND temp.corpId = corpId
        )
        WHERE row_num BETWEEN v_start_index AND v_end_index;

END HIRE_PURCHASE_ACCOUNT_LISTING;