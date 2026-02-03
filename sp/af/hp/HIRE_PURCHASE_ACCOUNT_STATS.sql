create or replace PROCEDURE HIRE_PURCHASE_ACCOUNT_STATS (
    userId      IN VARCHAR2,
    corpId      IN VARCHAR2,
    CURSOR      OUT SYS_REFCURSOR
) AS
    baseCcy VARCHAR2(3);

    -- Declare the nested table for conversion rates
    conversionRates ConversionRateTable := ConversionRateTable();
BEGIN
    -- Fetch base currency by calling the GET_BASE_CURRENCY function
    baseCcy := GET_BASE_CURRENCY(corpId);

    -- Fetch conversion rates by calling the GET_CONVERSION_RATES function
    conversionRates := GET_CONVERSION_RATES(corpId);

    OPEN CURSOR FOR
        SELECT
            SUM(numberOfAcct) AS numberOfAcct,
            SUM(financedAmt) AS financedAmt,
            SUM(outstandingBalance) AS outstandingBalance,
            MAX(lastUpdatedTime) AS lastUpdatedTime,
            MAX(dataPresentedOn) AS dataPresentedOn,
            baseCcy AS currency
        FROM (
            -- Parent query
            SELECT
                COUNT(DISTINCT php.ACCOUNT_CHILD) AS numberOfAcct,
                SUM(DISTINCT Convert_Currency(baseCcy, alhp.CURRENCY, php.UTILISED_AMOUNT, conversionRates)) AS financedAmt,
                SUM(DISTINCT Convert_Currency(baseCcy, alhp.CURRENCY, php.OUTSTANDING_BALANCE, conversionRates)) AS outstandingBalance,
                MAX(TO_CHAR(COALESCE(alhp.DT_UPDATED, alhp.DT_CREATED), 'DD/MM/YYYY, HH24:MI:SS ("GMT"TZH:TZM)')) AS lastUpdatedTime,
                MAX(TO_CHAR(COALESCE(alhp.DT_UPDATED, alhp.DT_CREATED), 'DD MON YYYY')) AS dataPresentedOn
            FROM
                MV_USER_ENTITLEMENT mv
                INNER JOIN TBL_ACCT_LINK_BHP_HP alhp ON mv.acct_no = alhp.BHP_ACCOUNT_NO
                INNER JOIN TBL_PORTFOLIO_HP php ON alhp.BHP_ACCOUNT_NO = php.FACILITY_PARENT
            WHERE
                mv.sub_product = 'HP'
                AND php.ACCOUNT_CHILD IS NOT NULL
                AND mv.corp_usr_id = userId
                AND mv.corp_id = corpId

            UNION ALL

            -- Child query
            SELECT
                COUNT(DISTINCT php.ACCOUNT_CHILD) AS numberOfAcct,
                SUM(Convert_Currency(baseCcy, php.FACILITY_CURRENCY, php.UTILISED_AMOUNT, conversionRates)) AS financedAmt,
                SUM(Convert_Currency(baseCcy, php.FACILITY_CURRENCY, php.OUTSTANDING_BALANCE, conversionRates)) AS outstandingBalance,
                MAX(TO_CHAR(COALESCE(php.DT_UPDATED, php.DT_CREATED), 'DD/MM/YYYY, HH24:MI:SS ("GMT"TZH:TZM)')) AS lastUpdatedTime,
                MAX(TO_CHAR(COALESCE(php.DT_UPDATED, php.DT_CREATED), 'DD MON YYYY')) AS dataPresentedOn
            FROM
                MV_USER_ENTITLEMENT mv
                -- INNER JOIN TBL_ACCT_LINK_BHP_HP alhp ON mv.acct_no = alhp.HP_ACCOUNT_NO
                INNER JOIN TBL_PORTFOLIO_HP php ON mv.acct_no = php.ACCOUNT_CHILD
            WHERE
                mv.sub_product = 'HP'
                AND php.FACILITY_PARENT IS NULL
                AND mv.corp_usr_id = userId
                AND mv.corp_id = corpId
        );

END HIRE_PURCHASE_ACCOUNT_STATS;