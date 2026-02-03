create or replace PROCEDURE LOAN_OD_ACCT_DTL (
    accountNumber IN NVARCHAR2,
    userId        IN NVARCHAR2,
    corpId        IN NVARCHAR2,
    CURSOR OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN CURSOR FOR
        SELECT
            mv.ACCT_NO AS accountNumber,
            od.CURRENCY_CODE AS currency,
            od.UTILIZED_AMOUNT  AS utilisedAmount,
            od.UNUTILIZED_AMOUNT  AS unutilisedAmount,
            DECODE(od.TEMPORARY_OD,
                0,od.FACILITY_LIMIT,
                od.TEMPORARY_OD
            ) AS overdraftLimitAmount,
            od.TEMPORARY_OD AS intradayODLimitAmount,
            DECODE(od.TEMPORARY_OD,
                0,'Normal',
                'Intraday'
            ) AS overdraftType
        FROM
            MV_USER_ENTITLEMENT mv
            INNER JOIN TBL_PORTFOLIO_DP od ON mv.ACCT_NO = od.ACCOUNT_NO
        WHERE
            mv.corp_usr_id = userId
            AND mv.corp_id = corpId
            AND mv.ACCT_NO = accountNumber
            AND od.IS_OD = 'Y';
END LOAN_OD_ACCT_DTL;