create or replace PROCEDURE LOAN_TLF_ACCT_STATS (
    userId IN NVARCHAR2,
    corpId IN NVARCHAR2,
    CURSOR OUT SYS_REFCURSOR
) AS
    maxDate TIMESTAMP(6);
    baseCcy VARCHAR2(3);
    conversionRates ConversionRateTable := ConversionRateTable();
   acctType NVARCHAR2(10);
BEGIN

    baseCcy := GET_BASE_CURRENCY(corpId);
    conversionRates := GET_CONVERSION_RATES(corpId);
    acctType := 'TL';

    SELECT MAX(COALESCE(DT_CREATED, DT_UPDATED)) INTO maxDate
        FROM MV_USER_ENTITLEMENT mv
        LEFT JOIN TBL_PORTFOLIO_LN ln ON mv.acct_no = ln.ACCOUNT_NO
    WHERE
        mv.provider = 'MY'
        AND mv.corp_usr_id = userId
        AND mv.corp_id = corpId
        AND mv.sub_product = acctType;

    OPEN CURSOR FOR
        SELECT
            COUNT(DISTINCT ln.ACCOUNT_NO) AS numberOfAccounts,
            SUM(Convert_Currency(baseCcy, ln.currency_code, ln.DRAWDOWN_AMOUNT, conversionRates)) AS consolidatedDrawdownAmount,
            SUM(Convert_Currency(baseCcy, ln.currency_code, ln.OUTSTANDING_BALANCE, conversionRates)) AS consolidatedOutstandingBalanceAmount,
            TO_CHAR(SYSDATE-1, 'DD/MM/YYYY') || ', 23:59:59 (GMT+8)' AS lastUpdatedDateTime,
            MAX(TO_CHAR((SELECT SYSTIMESTAMP - INTERVAL '1' DAY FROM dual), 'DD Mon YYYY')) AS lastUpdatedDate,
            MAX(mv.currency) AS currency
        FROM
            MV_USER_ENTITLEMENT mv
            LEFT JOIN TBL_PORTFOLIO_LN ln ON mv.acct_no = ln.ACCOUNT_NO
        WHERE
            mv.provider = 'MY'
            AND mv.corp_usr_id = userId
            AND mv.corp_id = corpId
            AND mv.sub_product = acctType
            AND (DT_CREATED = maxDate OR DT_UPDATED = maxDate);
--    UNION ALL
--        SELECT
--            COUNT(DISTINCT ln.ACCOUNT_NO) AS numberOfAccounts,
--            SUM(Convert_Currency(baseCcy, ln.currency_code, ln.DRAWDOWN_AMOUNT, conversionRates)) AS consolidatedDrawdownAmount,
--            SUM(Convert_Currency(baseCcy, ln.currency_code, ln.OUTSTANDING_BALANCE, conversionRates)) AS consolidatedOutstandingBalanceAmount,
--            MAX(TO_CHAR(COALESCE(ln.DT_UPDATED, ln.DT_CREATED), 'DD/MM/YYYY, HH24:MI:SS ("GMT"TZH:TZM)')) AS lastUpdatedDateTime,
--            MAX(TO_CHAR((SELECT SYSTIMESTAMP - INTERVAL '1' DAY FROM dual), 'DD Mon YYYY')) AS lastUpdatedDate,
--            MAX(mv.currency) AS currency
--        FROM
--            MV_USER_ENTITLEMENT mv
--            LEFT JOIN TBL_PORTFOLIO_LN ln ON mv.cif_no = ln.cif_number
--        WHERE
--            mv.provider = 'SG'
--            AND mv.corp_usr_id = userId
--            AND mv.corp_id = corpId
--            AND mv.sub_product = acctType;
END LOAN_TLF_ACCT_STATS;