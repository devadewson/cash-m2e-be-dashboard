create or replace PROCEDURE LOAN_RC_ACCT_DTL_MY (
    accountNumber IN NVARCHAR2,
    userId        IN NVARCHAR2,
    corpId        IN NVARCHAR2,
    CURSOR OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN CURSOR FOR
        SELECT
            mv.ACCT_NM AS accountName,
            mv.ACCT_NO AS accountNumber,
            strc.CURRENCY_CODE AS accountCurrency,
            mv.corp_short_nm AS accountOwnerCompany,
            strc.DT_CREATED AS lastUpdated,
            strc.FACILITY_LIMIT AS drawdownAmount,
            strc.OUTSTANDING_BALANCE AS outstandingBalanceAmount,
            mv.PROD_NM AS productName,
            strc.PRODUCT_TYPE AS productTypeCode,
            strc.ISLAMIC_FLAG AS islamicFlag,
            strc.WORK_ITEM_NO AS workItemNumber,
            strc.MATURITY_DATE AS maturityDate
        FROM
            MV_USER_ENTITLEMENT mv
            INNER JOIN TBL_PORTFOLIO_STRC strc ON mv.CIF_NO = strc.CIF_NO
        WHERE
            mv.corp_usr_id = userId
            AND mv.corp_id = corpId
            AND mv.ACCT_NO = accountNumber
            AND mv.product = 'LOAN'
            AND mv.sub_product = 'RC'
            AND mv.provider = 'MY';
END LOAN_RC_ACCT_DTL_MY;