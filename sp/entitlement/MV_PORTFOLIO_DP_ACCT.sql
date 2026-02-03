CREATE MATERIALIZED VIEW MV_PORTFOLIO_DP_ACCT
REFRESH ON DEMAND AS
SELECT account_no, currency_code, ledger_balance, facility_limit, dt_created
FROM tbl_portfolio_dp a
WHERE dt_created = (
    SELECT MAX(dt_created)
    FROM tbl_portfolio_dp
    WHERE account_no = a.account_no
);
/
