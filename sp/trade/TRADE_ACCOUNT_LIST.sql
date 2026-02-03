create or replace PROCEDURE TRADE_ACCOUNT_LIST
(
  pageNumber IN NUMBER,
  pageSize IN NUMBER,
  corpId IN VARCHAR2,
  userId IN VARCHAR2,
  tradeCategory IN VARCHAR2,
  acctType IN VARCHAR2,
  searchKey IN VARCHAR2,
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
    , tad.item as numberOfItems
    , tad.currency as currency
    , trim(to_char(tad.outstanding,'999,999,999,999.99')) as outstandingBalance
    , ROW_NUMBER() OVER (ORDER BY UPPER(mv.cif_nm)) as row_num
    , UPPER(mv.cif_nm) AS uppercaseAccountCifName
    from tbl_portfolio_trd tad inner join
    MV_USER_ENTITLEMENT mv
    ON mv.cif_no = tad.cif_no where mv.corp_usr_id = userId and mv.corp_id = corpId and mv.product = 'TRD'
    and tad.category = tradeCategory and (UPPER(mv.cif_no) LIKE '%' || UPPER(searchKey) || '%' OR UPPER(tad.currency) LIKE '%' || UPPER(searchKey))
    ORDER BY uppercaseAccountCifName ASC)
    WHERE row_num BETWEEN v_start_index AND v_end_index;
END TRADE_ACCOUNT_LIST;