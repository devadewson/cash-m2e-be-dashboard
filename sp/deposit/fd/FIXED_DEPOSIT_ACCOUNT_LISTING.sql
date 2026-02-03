create or replace NONEDITIONABLE PROCEDURE FIXED_DEPOSIT_ACCOUNT_LISTING (
    searchParam     IN VARCHAR2,
    userId          IN VARCHAR2,
    corpId          IN VARCHAR2,
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
                COUNT(*) OVER () AS totalCount,
                mv.acct_nm AS accountName,
                pfd.ACCOUNT_NO AS accountNumber,
                mv.currency,
                pfd.noOfCertificates,
                pfd.totalPrincipalAmt,
                ROW_NUMBER() OVER (ORDER BY pfd.ACCOUNT_NO) AS row_num
            FROM
                MV_USER_ENTITLEMENT mv
            INNER JOIN (
                SELECT ACCOUNT_NO, COUNT(DEPOSIT_NO) AS noOfCertificates, SUM(PRINCIPAL_AMOUNT) AS totalPrincipalAmt
                FROM TBL_PORTFOLIO_FD
                GROUP BY ACCOUNT_NO
            ) pfd ON mv.acct_no = pfd.ACCOUNT_NO
            WHERE
                mv.corp_usr_id = userId AND mv.corp_id = corpId
                AND (NVL2(searchParam, mv.corp_short_nm, mv.corp_short_nm) LIKE NVL2(searchParam, '%'|| searchParam || '%', mv.corp_short_nm)
                    OR NVL2(searchParam, pfd.ACCOUNT_NO, pfd.ACCOUNT_NO) LIKE NVL2(searchParam, '%'|| searchParam || '%', pfd.ACCOUNT_NO))
        )
        WHERE row_num BETWEEN v_start_index AND v_end_index;
END FIXED_DEPOSIT_ACCOUNT_LISTING;