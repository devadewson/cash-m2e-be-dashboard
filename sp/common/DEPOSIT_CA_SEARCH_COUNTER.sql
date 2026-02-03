create or replace PROCEDURE DEPOSIT_CA_SEARCH_COUNTER
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
        LEFT JOIN TBL_PORTFOLIO_DP pdp ON mv.acct_no = pdp.account_no
        WHERE
        mv.corp_usr_id = userId
        AND mv.corp_id = corpId
        AND mv.product = 'DEPOSIT'
        AND mv.sub_product IN ('CA', 'FCA', 'MFCA')
        AND mv.is_add_oth_bank_acct = 'N'
        AND (
            UPPER(mv.acct_nm) LIKE '%' || UPPER(searchParam) || '%'
            OR (NVL2(searchParam, pdp.account_no, pdp.account_no) LIKE NVL2(searchParam, '%' || searchParam || '%', pdp.account_no))
        );
END DEPOSIT_CA_SEARCH_COUNTER;
/