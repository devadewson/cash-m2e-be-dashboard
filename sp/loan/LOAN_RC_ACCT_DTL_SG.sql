create or replace NONEDITIONABLE PROCEDURE LOAN_RC_ACCT_DTL_SG (
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
            strc.REPAYMENT_AMT AS repaymentAmount,
            strc.OUTSTANDING_LOAN_AMT AS outstandingLoanAmount,
            strc.INTEREST_AMT AS interestAmount,
            mv.PROD_NM AS productName,
            strc.LATE_INTEREST_AMT AS loanInterestAmount,
            strc.ORIGINAL_LOAN_AMT AS originalLoanAmount,
            strc.INTEREST_RATE AS interestRate,
            strc.NEXT_INTEREST_REPAYMENT_DATE AS nextInterestRepaymentDate,
            strc.NEXT_INTEREST_REFIX_DATE AS nextInterestRefixDate
        FROM
            MV_USER_ENTITLEMENT mv
            INNER JOIN TBL_PORTFOLIO_STRC strc ON mv.CIF_NO = strc.CIF_NO
        WHERE
            mv.corp_usr_id = userId
            AND mv.corp_id = corpId
            AND mv.ACCT_NO = accountNumber
            AND mv.product = 'LOAN'
            AND mv.sub_product = 'RC'
            AND mv.provider = 'SG';
END LOAN_RC_ACCT_DTL_SG;