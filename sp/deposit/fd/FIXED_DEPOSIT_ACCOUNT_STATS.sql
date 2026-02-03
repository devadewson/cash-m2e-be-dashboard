create or replace PROCEDURE FIXED_DEPOSIT_ACCOUNT_STATS (
    userId      IN VARCHAR2,
    corpId      IN VARCHAR2,
    CURSOR      OUT SYS_REFCURSOR
) AS
    numberOfAcct NUMBER;
    totalOfCertificates NUMBER;
    consolidatedPrincipalAmt NUMBER := 0;
    baseCcy VARCHAR2(3);
    formattedLastUpdatedTime VARCHAR2(50);
    currency VARCHAR(10);

    -- Declare the nested table for conversion rates
    conversionRates ConversionRateTable := ConversionRateTable();

BEGIN

    -- Fetch base currency by calling the GET_BASE_CURRENCY function
    baseCcy := GET_BASE_CURRENCY(corpId);
    -- Fetch conversion rates by calling the GET_CONVERSION_RATES function
    conversionRates := GET_CONVERSION_RATES(corpId);

    OPEN CURSOR FOR
    SELECT
        COUNT(DISTINCT pfd.ACCOUNT_NO) AS numberOfAcct,
        COUNT(pfd.DEPOSIT_NO) AS totalOfCertificates,
        SUM(CONVERT_CURRENCY(baseCcy, pfd.currency_code, pfd.PRINCIPAL_AMOUNT, conversionRates)) AS consolidatedPrincipalAmt,
        MAX(TO_CHAR(COALESCE(pfd.DT_UPDATED, pfd.DT_CREATED), 'DD/MM/YYYY, HH24:MI:SS ("GMT"TZH:TZM)')) AS lastUpdatedTime,
        MAX(mv.currency) AS currency
    FROM
        MV_USER_ENTITLEMENT mv
        INNER JOIN TBL_PORTFOLIO_FD pfd ON mv.acct_no = pfd.ACCOUNT_NO
    WHERE
        mv.corp_usr_id = userId
        AND mv.corp_id = corpId
        AND mv.SUB_PRODUCT = 'FD';

END FIXED_DEPOSIT_ACCOUNT_STATS;