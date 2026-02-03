create or replace NONEDITIONABLE PROCEDURE OTHER_BANK_DEPOSIT_ACCOUNT_STATS (
    userId          IN NVARCHAR2,
    corpId          IN NVARCHAR2,
    CURSOR OUT SYS_REFCURSOR
) AS
    baseCcy NVARCHAR2(3);
    conversionRates ConversionRateTable := ConversionRateTable();
BEGIN

    baseCcy := GET_BASE_CURRENCY(corpId);
    conversionRates := GET_CONVERSION_RATES(corpId);

OPEN CURSOR FOR
    SELECT
        ROUND(SUM(CONVERT_CURRENCY_NEW(corpId, baseCcy, pas.clo_bal_curr, pas.closingBalance)), 2) AS consolidatedClosingBalance,
        COUNT(distinct mv.acct_no) AS noOfAccount,
        MAX(mv.currency) AS currency,
        TO_CHAR(TRUNC(MAX(COALESCE(COALESCE(pas.dt_updated, pas.dt_created), sysdate)))-1, 'DD/MM/YYYY') || ', 23:59:59 (GMT+8)' AS lastUpdatedDateTime
    FROM MV_USER_ENTITLEMENT mv
        LEFT JOIN(
                SELECT account_id, clo_bal_curr , SUM(clo_bal_amount) as closingBalance, MAX(dt_created) as dt_created , MAX(txn_datetime) as dt_updated, mt_type
                FROM tbl_portfolio_activity_swift
                GROUP BY account_id, clo_bal_curr, mt_type
        ) pas ON mv.acct_no = pas.account_id
        WHERE
            mv.corp_usr_id = userId
            AND mv.corp_id = corpId
            AND mv.is_add_oth_bank_acct = 'Y'
            AND mv.product = 'DEPOSIT';
END OTHER_BANK_DEPOSIT_ACCOUNT_STATS;