create or replace NONEDITIONABLE PROCEDURE FIXED_DEPOSIT_ACCOUNT_DETAIL (
    acctNumber  IN VARCHAR2,
    userId      IN VARCHAR2,
    corpId      IN VARCHAR2,
    CURSOR      OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN CURSOR FOR
        SELECT
            mv.acct_nm AS accountName,
            mv.corp_short_nm AS companyName,
            pfd.ACCOUNT_NO AS accountNo,
            pfd.CURRENCY_CODE AS currency,
            TO_CHAR(COALESCE(pfd.DT_UPDATED, pfd.DT_CREATED), 'DD/MM/YYYY, HH24:MI:SS ("GMT"TZH:TZM)')  AS lastUpdatedDateTime,
            pfd.PRODUCT_DESCRIPTION AS productName,
            pfd.totalPrincipalAmt AS principalAmount,
            pfd.noOfCertificates
        FROM
            MV_USER_ENTITLEMENT mv
            INNER JOIN (
                SELECT
                    ACCOUNT_NO,
                    COUNT(DEPOSIT_NO) AS noOfCertificates,
                    SUM(PRINCIPAL_AMOUNT) AS totalPrincipalAmt,
                    MAX(PRODUCT_DESCRIPTION) AS PRODUCT_DESCRIPTION,
                    MAX(CURRENCY_CODE) AS CURRENCY_CODE,
                    MAX(DT_UPDATED) AS DT_UPDATED,
                    MAX(DT_CREATED) AS DT_CREATED
                FROM
                    TBL_PORTFOLIO_FD
                GROUP BY
                    ACCOUNT_NO
            ) pfd ON mv.acct_no = pfd.ACCOUNT_NO
        WHERE
            mv.corp_usr_id = userId AND mv.corp_id = corpId
            AND pfd.ACCOUNT_NO = acctNumber;
END FIXED_DEPOSIT_ACCOUNT_DETAIL;