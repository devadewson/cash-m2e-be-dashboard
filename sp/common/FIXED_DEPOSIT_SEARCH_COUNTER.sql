create or replace PROCEDURE FIXED_DEPOSIT_SEARCH_COUNTER
(
    corpId IN NVARCHAR2,
    userId IN NVARCHAR2,
    searchParam IN NVARCHAR2,
    CURSOR OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN CURSOR FOR
        SELECT COUNT(*) as totalSearchCount
        FROM MV_USER_ENTITLEMENT mv
        LEFT JOIN TBL_PORTFOLIO_FD pfd ON mv.acct_no = pfd.account_no
        WHERE
        mv.corp_usr_id = userId
        AND mv.corp_id = corpId
        AND mv.product = 'DEPOSIT'
        AND mv.sub_product = 'FD'
        AND (
            UPPER(mv.acct_nm) LIKE '%' || UPPER(searchParam) || '%'
            OR (NVL2(searchParam, pfd.account_no, pfd.account_no) LIKE NVL2(searchParam, '%' || searchParam || '%', pfd.account_no))
        );
END FIXED_DEPOSIT_SEARCH_COUNTER;
/