create or replace PROCEDURE DEPOSIT_CA_EOD_BALANCE (
  pageNumber IN NUMBER,
  pageSize IN NUMBER,
  acctNumber IN VARCHAR2,
  month IN NUMBER,
  year IN NUMBER,
  CURSOR OUT SYS_REFCURSOR
) AS
   v_total_page NUMBER;
   v_record_size NUMBER;
   v_start_index NUMBER;
   v_end_index NUMBER;
BEGIN
    -- calculate pagination index
    v_start_index := (pageNumber - 1) * pageSize + 1;
    v_end_index := pageNumber * pageSize;
OPEN CURSOR FOR
    select * from (
        select COUNT(*) OVER() as totalCount,
        TO_CHAR(pdp.dt_created, 'DD Mon YYYY') AS createdDate,
        pdp.ledger_balance AS closingLedgerBalance,
        pdp.cust_available_bal AS closingAvailableBalance,
        pdp.currency_code AS currency,
        ROW_NUMBER() OVER (ORDER BY pdp.dt_created) as row_num
    FROM
        TBL_PORTFOLIO_DP pdp
    WHERE
        pdp.account_no = acctNumber
        AND EXTRACT(MONTH FROM pdp.dt_created) = month
        AND EXTRACT(YEAR FROM pdp.dt_created) = year
    ORDER BY
        pdp.dt_created DESC)
    WHERE row_num BETWEEN v_start_index AND v_end_index;
END DEPOSIT_CA_EOD_BALANCE;