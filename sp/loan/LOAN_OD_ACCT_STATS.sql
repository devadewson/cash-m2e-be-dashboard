create or replace PROCEDURE LOAN_OD_ACCT_STATS (
    userId IN NVARCHAR2,
    corpId IN NVARCHAR2,
    CURSOR OUT SYS_REFCURSOR
) AS
    maxDate TIMESTAMP(6);
    baseCcy VARCHAR2(3);
    conversionRates ConversionRateTable := ConversionRateTable();
BEGIN
    baseCcy := GET_BASE_CURRENCY(corpId);
    conversionRates := GET_CONVERSION_RATES(corpId);

    SELECT MAX(COALESCE(DT_CREATED, DT_UPDATED)) INTO maxDate FROM MV_USER_ENTITLEMENT mv LEFT JOIN TBL_PORTFOLIO_DP od ON mv.acct_no = od.ACCOUNT_NO
        WHERE mv.corp_usr_id = userId
        AND mv.corp_id = corpId
        AND od.IS_OD = 'Y';

    OPEN CURSOR FOR
        SELECT
            COUNT(DISTINCT od.ACCOUNT_NO) AS numberOfAccounts,
            SUM(Convert_Currency(baseCcy, od.currency_code, od.FACILITY_LIMIT, conversionRates)) AS consolidatedOverdraftLimitAmount,
            SUM(Convert_Currency(baseCcy, od.currency_code, od.UTILIZED_AMOUNT, conversionRates)) AS consolidatedUtilisedAmount,
            TO_CHAR(SYSDATE-1, 'DD/MM/YYYY') || ', 23:59:59 (GMT+8)' AS lastUpdatedDateTime,
            MAX(TO_CHAR((SELECT SYSTIMESTAMP - INTERVAL '1' DAY FROM dual), 'DD Mon YYYY')) AS lastUpdatedDate,
            MAX(mv.currency) AS currency
        FROM
            MV_USER_ENTITLEMENT mv
            LEFT JOIN TBL_PORTFOLIO_DP od ON mv.acct_no = od.ACCOUNT_NO
        WHERE
            mv.corp_usr_id = userId
            AND mv.corp_id = corpId
            AND od.IS_OD = 'Y'
            AND (DT_CREATED = maxDate OR DT_UPDATED = maxDate);
END LOAN_OD_ACCT_STATS;