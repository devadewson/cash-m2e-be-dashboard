create or replace NONEDITIONABLE PROCEDURE LOAN_TLF_ACCT_DTL (
    accountNumber IN NVARCHAR2,
    hostProductCode IN NVARCHAR2,
    userId        IN NVARCHAR2,
    corpId        IN NVARCHAR2,
    CURSOR OUT SYS_REFCURSOR
) AS
BEGIN
    OPEN CURSOR FOR
        SELECT
            mv.ACCT_NM AS accountName,
            mv.ACCT_NO AS accountNumber,
            ln.CURRENCY_CODE AS accountCurrency,
            mv.corp_short_nm AS accountOwnerCompany,
            ln.DT_CREATED AS lastUpdated,
            ln.APPROVED_AMOUNT AS approvedAmount,
            ln.DRAWDOWN_AMOUNT AS drawdownAmount,
            ln.OUTSTANDING_BALANCE AS outstandingBalanceAmount,
            mv.PROD_NM AS productName,
            ln.PRODUCT_TYPE AS productType,
            ln.PRODUCT_TERM AS productTerm,
            ln.INSTALLMENT_AMOUNT AS installmentAmount,
            ln.INSTALL_CURRENCY AS installmentCurrency,
            ln.INSTALL_DUE_DATE AS installmentDueDate,
            ln.INSTALL_FREQUENCY AS installmentFrequency,
            ln.FIRST_DRAWDOWN_DATE AS firstDrawdownDate,
            ln.LAST_DRAWDOWN_DATE AS lastDrawdownDate,
            ln.PAYMENT_OPTION AS paymentOption,
            ln.ADVANCE_PAYMENT AS paymentInAdvanceAmount,
            ln.ADVANCE_CURRENCY AS paymentInAdvanceCurrency,
            ln.MISCELLANEOUS_CHARGES AS miscellaneousCharges,
            ln.INTEREST_RATE AS interestRate,
            ln.INTEREST_AMT AS interestDueAmount,
            ln.INTEREST_CUR AS interestDueCurrency,
            ln.INTEREST_DUE_DATE AS interestDueDate,
            ln.INTEREST_FREQ AS interestFrequency,
            ln.COMMITMENT_FEES AS commitmentFees,
            ln.PENALTY_INTEREST AS penaltyInterest,
            ln.PROFIT_RATE AS profitRate,
            ln.PROFIT_FREQ AS profitFrequency,
            ln.PROFIT_DUE AS profitDueDate,
            ln.FLOAT_OUTSTANDING AS outstandingProgressiveProfit,
            ln.COST_OF_FIN_OUTSTANDING AS outstandingCostOfFinance,
            ln.SELLING_PRICE AS sellingPrice,
            ln.PROFIT_COST AS profitOnCostOfFunds,
            ln.LAST_CHARGES AS lastPaymentCharges,
            ln.BULLET_MATURITY_DATE AS bulletMaturityDate,
            ln.PENALTY_ON_PRINCIPAL AS penaltyOnPrincipal,
            ln.PROFIT_DUE AS profitPaymentDue,
            ln.SELL_PRICE_AMOUNT AS sellingPriceAmount,
            ln.SELL_PRICE_CURRENCY AS sellingPriceCurrency,
            ln.PROFIT_OUTST_AMOUNT AS profitOnCostOutstandingAmount,
            ln.PROFIT_OUTST_CURRENCY AS profitOnCostOutstandingCurrency,
            ln.LATE_PAYMENT_CHARGES AS latePaymentChargesOnCOF,
            ln.LATE_CHARGES_PROFIT AS latePaymentChargesOnProfitAmount,
            ln.LATE_CHARGES_PROFIT_CUR AS latePaymentChargesOnProfitCurrency,
            ln.MONTHLY_INSTALL_AMOUNT AS monthlyInstallmentAmount,
            ln.MONTHLY_INSTALL_CUR AS monthlyInstallmentCurrency,
            ln.NEXT_PAYMENT_DATE AS nextPaymentDueDate,
            ln.LAST_PAYMENT_DATE AS lastPaymentDate,
            ln.INTEREST_AMOUNT AS interestAmount,
            ln.INTEREST_CURRENCY AS interestCurrency,
            ln.DEFAULT_LATE_INT AS defaultLateInterest,
            ln.PAYMENT_TYPE AS paymentType,
            ln.TOTAL_PRINCIPAL_BILLED AS totalPrincipalAmountBilled,
            ln.TOTAL_PRINCIPAL_CUR AS totalPrincipalAmountBilledCurrency,
            ln.ORIGINAL_LOAN_AMOUNT AS originalLoanAmount,
            ln.ORIGINAL_LOAN_CUR AS originalLoanCurrency,
            ln.LATE_INTEREST_AMOUNT AS lateInterestAmount,
            ln.LATE_INTEREST_CUR AS lateInterestCurrency,
            ln.NEXT_INTEREST_DATE AS nextInterestPaymentDate,
            ln.ORIGINAL_LOAN_AMOUNT AS overdueInstallmentAmount,
            ln.ORIGINAL_LOAN_CUR AS overdueInstallmentCurrency,
            ln.ORIGINAL_FIN_AMOUNT AS originalFinancingAmount,
            ln.ORIGINAL_FIN_CUR AS originalFinancingCurrency,
            ln.REPAYMENT_AMOUNT AS repaymentAmount,
            ln.REPAYMENT_CUR AS repaymentCurrency,
            ln.LATE_PAYMENT_CHARGES AS latePaymentCharges,
            ln.NEXT_PROFIT_DATE AS nextProfitRepaymentDate,
            ln.NEXT_PRINCIPAL_DATE AS nextPrincipalRepaymentDate
        FROM
            MV_USER_ENTITLEMENT mv
            INNER JOIN TBL_PORTFOLIO_LN ln ON mv.CIF_NO = ln.CIF_NUMBER
        WHERE
            mv.corp_usr_id = userId
            AND mv.corp_id = corpId
            AND mv.ACCT_NO = accountNumber
            AND mv.product = hostProductCode
            AND mv.sub_product = 'TL'
            AND mv.provider = 'MY';
END LOAN_TLF_ACCT_DTL;