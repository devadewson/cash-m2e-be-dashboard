create or replace NONEDITIONABLE PROCEDURE FIXED_DEPOSIT_CERTIFICATE_LISTING (
    acctNumber  IN VARCHAR2,
    userId      IN VARCHAR2,
    corpId      IN VARCHAR2,
    pageNumber      IN NUMBER,
    pageSize        IN NUMBER,
    CURSOR      OUT SYS_REFCURSOR
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
                COUNT(*) OVER() as totalCount,
                pfd.DEPOSIT_NO AS certificateNumber,
                pfd.INTEREST_RATE AS profitRate,
                pfd.MATURITY_DATE AS maturityDate,
                pfd.PRINCIPAL_AMOUNT AS principalAmount,
                pfd.CURRENCY_CODE AS currency,
                ROW_NUMBER() OVER (ORDER BY pfd.ACCOUNT_NO) AS row_num
            FROM
                MV_USER_ENTITLEMENT mv
                INNER JOIN TBL_PORTFOLIO_FD pfd ON mv.acct_no = pfd.ACCOUNT_NO
            WHERE
                mv.corp_usr_id = userId AND mv.corp_id = corpId
                AND pfd.ACCOUNT_NO = acctNumber
        )
        WHERE row_num BETWEEN v_start_index AND v_end_index;
END FIXED_DEPOSIT_CERTIFICATE_LISTING;