create or replace PROCEDURE TRADE_SEARCH_COUNTER
(
    corpId IN NVARCHAR2,
    userId IN NVARCHAR2,
    searchParam IN NVARCHAR2,
    subProduct IN NVARCHAR2,
    CURSOR OUT SYS_REFCURSOR
)
AS
BEGIN
    OPEN CURSOR FOR
        SELECT COUNT(*) as totalSearchCount
        FROM MV_USER_ENTITLEMENT mv
        LEFT JOIN TBL_PORTFOLIO_TRD trd ON mv.cif_no = trd.cif_no
        WHERE
        mv.corp_usr_id = userId
        AND mv.corp_id = corpId
        AND mv.product = 'TRADE'
        AND trd.category = subProduct
        AND (
            UPPER(mv.acct_nm) LIKE '%' || UPPER(searchParam) || '%'
            OR (NVL2(searchParam, trd.item, trd.item) LIKE NVL2(searchParam, '%' || searchParam || '%', trd.my_product_description))
        );
END TRADE_SEARCH_COUNTER;
/

