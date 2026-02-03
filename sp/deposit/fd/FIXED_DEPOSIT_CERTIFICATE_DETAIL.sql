create or replace NONEDITIONABLE PROCEDURE FIXED_DEPOSIT_CERTIFICATE_DETAIL (
    certificateNumber   IN VARCHAR2,
    userId              IN VARCHAR2,
    corpId              IN VARCHAR2,
    CURSOR              OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN CURSOR FOR
        SELECT
            pfd.DEPOSIT_NO AS certificateNumber,
            pfd.PRINCIPAL_AMOUNT AS principalAmount,
            pfd.CURRENCY_CODE AS currency,
            pfd.PLACEMENT_DATE AS placementDate,
            pfd.PERIOD AS tenure,
            pfd.INTEREST_RATE AS interestRate,
            pfd.MATURITY_INTEREST AS maturityInterest,
            pfd.MATURITY_DATE AS maturityDate,
            pfd.MATURITY_INSTRUCTION AS instructionOnMaturity,
            pfd.INTEREST_PAYMENT_MODE AS interestPaymentMode,
            '-' AS interestCreditNumber,
            pfd.TERM AS term,
            pfd.INTEREST_RATE AS profitRate
        FROM
            MV_USER_ENTITLEMENT mv
            INNER JOIN TBL_PORTFOLIO_FD pfd ON mv.acct_no = pfd.ACCOUNT_NO
        WHERE
            mv.corp_usr_id = userId AND mv.corp_id = corpId
            AND pfd.DEPOSIT_NO = certificateNumber;
END FIXED_DEPOSIT_CERTIFICATE_DETAIL;