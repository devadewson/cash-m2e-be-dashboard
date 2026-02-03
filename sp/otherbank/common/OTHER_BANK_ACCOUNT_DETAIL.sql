create or replace NONEDITIONABLE PROCEDURE OTHER_BANK_ACCOUNT_DETAIL (
    acctNumber  IN VARCHAR2,
    userId      IN VARCHAR2,
    corpId      IN VARCHAR2,
    CURSOR      OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN CURSOR FOR
        SELECT
            mv.acct_nm AS acctName,
            pas.account_id AS acctNumber,
            pas.clo_bal_curr AS currency,
            TO_CHAR(pas.dt_created, 'DD/MM/YYYY, HH24:MI:SS ("GMT"TZH:TZM)')  AS lastUpdatedDateTime,
            pas.ope_bal_amount AS openingBalance,
            pas.clo_bal_amount AS closingBalance,
            mv.bank_nm AS bankName,
            mv.sub_product AS acctType,
            mv.swift_cd AS senderBIC,
            pas.mt_type AS msgType,
            pas.stat_ref_no AS statementReference,
            pas.statement_number AS statementNumber,
            CASE
                WHEN pas.ope_bal_date IS NULL AND pas.clo_bal_date IS NULL
                    THEN NULL
                WHEN pas.ope_bal_date IS NULL
                    THEN pas.clo_bal_date
                WHEN pas.clo_bal_date IS NULL
                    THEN pas.ope_bal_date
                WHEN pas.ope_bal_date = pas.clo_bal_date
                    THEN pas.ope_bal_date
                ELSE pas.ope_bal_date || ' - ' || pas.clo_bal_date
            END AS statementDate
        FROM
            MV_USER_ENTITLEMENT mv
        INNER JOIN tbl_portfolio_activity_swift pas ON mv.acct_no = pas.account_id
        WHERE
            mv.corp_usr_id = userId
            AND mv.corp_id = corpId
            AND pas.account_id = acctNumber
            AND pas.dt_created = (SELECT MAX(dt_created) FROM tbl_portfolio_activity_swift WHERE account_id = acctNumber);
END OTHER_BANK_ACCOUNT_DETAIL;