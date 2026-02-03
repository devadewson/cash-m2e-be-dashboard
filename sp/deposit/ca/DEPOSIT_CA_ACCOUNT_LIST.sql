create or replace NONEDITIONABLE PROCEDURE DEPOSIT_CA_ACCOUNT_LIST
(
  pageNumber IN NUMBER,
  pageSize IN NUMBER,
  corpId IN VARCHAR2,
  userId IN VARCHAR2,
  searchParam IN VARCHAR2,
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
  select COUNT(*) OVER() as totalCount
    , mv.acct_nm as acctName
    , pdp.account_no as acctNumber
    , pdp.currency_code as currency
    , trim(to_char(pdp.ledger_balance,'999,999,999,999.99')) as ledgerBalance
    , trim(to_char(pdp.cust_available_bal,'999,999,999,999.99')) as availableBalance
    , ROW_NUMBER() OVER (ORDER BY UPPER(mv.acct_nm)) as row_num
    from tbl_portfolio_dp pdp
    inner join MV_USER_ENTITLEMENT mv
    on mv.acct_no = pdp.account_no
    where mv.corp_usr_id = userId and mv.sub_product = 'CA' and mv.product = 'DEPOSIT'
    and mv.corp_id = corpId and (UPPER(mv.acct_nm) LIKE '%' || UPPER(searchParam) || '%' OR UPPER(pdp.account_no) LIKE '%' || UPPER(searchParam))
    ORDER BY UPPER(mv.acct_nm) ASC)
    WHERE row_num BETWEEN v_start_index AND v_end_index;
END DEPOSIT_CA_ACCOUNT_LIST;