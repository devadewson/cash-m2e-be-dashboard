create or replace PROCEDURE HIRE_PURCHASE_ACCOUNT_DETAIL (
    acctNumber      IN VARCHAR2,
    accountLevel    IN VARCHAR2,
    userId          IN VARCHAR2,
    corpId          IN VARCHAR2,
    CURSOR          OUT SYS_REFCURSOR
) AS
BEGIN
    IF accountLevel = 'MASTERACCOUNT' THEN
        OPEN CURSOR FOR
            SELECT
                MAX(mv.ACCT_NM) AS acctName,
                MAX(mv.CORP_SHORT_NM) AS companyName,
                acctNumber AS acctNumber,
                MAX(alhp.CURRENCY) AS currency,
                MAX(TO_CHAR(COALESCE(alhp.DT_UPDATED, alhp.DT_CREATED), 'DD/MM/YYYY, HH24:MI:SS ("GMT"TZH:TZM)')) AS lastUpdatedDateTime,
                SUM(DISTINCT php.UTILISED_AMOUNT) AS financedAmt,
                SUM(DISTINCT php.OUTSTANDING_BALANCE) AS outstandingBalance,
                SUM(php.FACILITY_LIMIT) AS facilityLimit,
                COUNT(DISTINCT php.ACCOUNT_CHILD) AS numberOfAcct
            FROM
                (
                    SELECT
                        ACCT_NM, ACCT_NO, CORP_USR_ID, CORP_ID, SUB_PRODUCT, CORP_SHORT_NM
                    FROM MV_USER_ENTITLEMENT
                ) mv
                INNER JOIN TBL_ACCT_LINK_BHP_HP alhp ON mv.acct_no = alhp.BHP_ACCOUNT_NO
                INNER JOIN TBL_PORTFOLIO_HP php ON alhp.BHP_ACCOUNT_NO = php.FACILITY_PARENT
            WHERE
                mv.SUB_PRODUCT = 'HP'
                AND php.ACCOUNT_CHILD IS NOT NULL
                AND mv.CORP_USR_ID = userId
                AND mv.CORP_ID = corpId
                AND alhp.BHP_ACCOUNT_NO = acctNumber;

    ELSIF accountLevel = 'ACCOUNT' THEN
        OPEN CURSOR FOR
            SELECT
                MAX(mv.ACCT_NM) AS acctName,
                MAX(mv.CORP_SHORT_NM) AS companyName,
                acctNumber AS acctNumber,
                MAX(php.FACILITY_CURRENCY) AS currency,
                MAX(TO_CHAR(COALESCE(alhp.DT_UPDATED, alhp.DT_CREATED), 'DD/MM/YYYY, HH24:MI:SS ("GMT"TZH:TZM)')) AS lastUpdatedDateTime,
                SUM(DISTINCT php.UTILISED_AMOUNT) AS financedAmt,
                SUM(DISTINCT php.OUTSTANDING_BALANCE) AS outstandingBalance,
                COUNT(DISTINCT php.ACCOUNT_CHILD) AS numberOfAcct
            FROM
                (
                    SELECT
                        ACCT_NM, ACCT_NO, CORP_USR_ID, CORP_ID, SUB_PRODUCT, CORP_SHORT_NM
                    FROM MV_USER_ENTITLEMENT
                ) mv
                INNER JOIN TBL_ACCT_LINK_BHP_HP alhp ON mv.acct_no = alhp.HP_ACCOUNT_NO
                INNER JOIN TBL_PORTFOLIO_HP php ON alhp.HP_ACCOUNT_NO = php.ACCOUNT_CHILD
            WHERE
                mv.SUB_PRODUCT = 'HP'
                AND php.FACILITY_PARENT IS NULL
                AND mv.CORP_USR_ID = userId
                AND mv.CORP_ID = corpId
                AND alhp.HP_ACCOUNT_NO = acctNumber;

    ELSE
        -- Handle the case where accountLevel is neither 'MASTER' nor 'ACCOUNT'
        RAISE_APPLICATION_ERROR(-20004, 'Incorrect Account Level Define');
    END IF;
END HIRE_PURCHASE_ACCOUNT_DETAIL;