create or replace PROCEDURE OTHER_BANK_LOAN_ACCOUNT_SUMMARY_LIST (
    searchParam     IN NVARCHAR2,
    userId          IN NVARCHAR2,
    corpId          IN NVARCHAR2,
    pageNumber      IN NUMBER,
    pageSize        IN NUMBER,
    CURSOR          OUT SYS_REFCURSOR
) AS
    v_start_index NUMBER;
    v_end_index   NUMBER;
BEGIN
    -- Calculate pagination index
    v_start_index := (pageNumber - 1) * pageSize + 1;
    v_end_index := pageNumber * pageSize;

    OPEN CURSOR FOR
        SELECT * FROM (
            SELECT
              mv.acct_no AS accountNumber,
              COUNT(DISTINCT mv.acct_no) OVER() AS totalCount,
              TO_CHAR(pas.dateTime, 'DD Mon YYYY HH:MI') AS dateTime,
              mv.acct_nm AS accountName,
              mv.currency AS currency,
              pas.messageType as messageType,
              mv.swift_cd as senderBIC,
              TRIM(to_char(pas.closingBalance,'999,999,999,999.99')) AS closingBalance,
              ROW_NUMBER() OVER (ORDER BY UPPER(mv.acct_nm)) AS row_num
            FROM MV_USER_ENTITLEMENT mv
            LEFT JOIN(
                    SELECT account_id, MAX(clo_bal_curr) as currency , SUM(clo_bal_amount) as closingBalance, MAX(dt_created) as dateTime, MAX(mt_type) as messageType
                    FROM tbl_portfolio_activity_swift
                    GROUP BY account_id
            ) pas ON mv.acct_no = pas.account_id
            WHERE
                mv.corp_usr_id = userId
                AND mv.corp_id = corpId
                AND mv.product = 'LOAN'
                AND mv.is_add_oth_bank_acct = 'Y'
                AND ((UPPER(mv.acct_nm) LIKE '%' || UPPER(searchParam) || '%')
                OR (UPPER(mv.currency) LIKE '%' || UPPER(searchParam) || '%')
                OR (NVL2(searchParam, mv.acct_no, mv.acct_no) LIKE NVL2(searchParam, '%'|| searchParam || '%', mv.acct_no)))
            ORDER BY UPPER(MV.acct_nm) ASC)
        WHERE row_num BETWEEN v_start_index AND v_end_index;

END OTHER_BANK_LOAN_ACCOUNT_SUMMARY_LIST;