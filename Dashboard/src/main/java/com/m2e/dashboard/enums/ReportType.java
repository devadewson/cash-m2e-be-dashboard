package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum ReportType {
    /* Loan */
    LOAN_OD_ACCOUNTLISTING_RPT_TYPE("Loan Overdraft - Account Listing Report Type"),
    LOAN_RC_ACCOUNTLISTING_RPT_TYPE("Loan Revolving Credit - Account Listing Report Type"),
    LOAN_RC_TRXHISTORIES_RPT_TYPE("Loan Revolving Credit - Transaction History Report Type"),
    LOAN_TLF_ACCOUNTLISTING_RPT_TYPE("Loan Term Loan Financing - Account Listing Report Type"),
    LOAN_TLF_TRXHISTORIES_RPT_TYPE("Loan Term Loan Financing - Transaction History Report Type"),
    LOAN_TLF_TRANCHES_RPT_TYPE("Loan Term Loan Financing - Tranches Report Type"),

    /* Deposit */
    DEPOSIT_CA_ACCOUNTLISTING_RPT_TYPE("Deposit Current Account - Account Listing Report Type"),
    DEPOSIT_CA_TRX_HISTORY_RPT_TYPE("Deposit Current Account - Transaction History Report Type"),
    DEPOSIT_CA_PAYMENT_HISTORY_RPT_TYPE("Deposit Current Account - Payment History Report Type"),
    DEPOSIT_CA_PAYMENT_DETAIL_RPT_TYPE("Deposit Current Account - Payment Details Report Type"),
    DEPOSIT_CA_EOD_BALANCE_RPT_TYPE("Deposit Current Account - EOD Balance Report Type"),
    DEPOSIT_CA_SWIFT_MT103_RPT_TYPE("Deposit Current Account - Swift MT103 Report Type"),
    DEPOSIT_CA_GPI_STATUS_RPT_TYPE("Deposit Current Account - GPI Status Report Type"),

    DEPOSIT_FD_ACCLISTING_RPT_TYPE("Deposit FD - Account Listing Report Type"),
    DEPOSIT_FD_CERTLISTING_RPT_TYPE("Deposit FD - Certificate Listing Report Type"),
    DEPOSIT_FD_TRXHISTORIES_RPT_TYPE("Deposit FD - Transaction History Report Type"),

    DEPOSIT_VA_ACCOUNTLISTING_RPT_TYPE("Deposit VA - Account Listing Report Type"),
    DEPOSIT_VA_TRXHISTORIES_RPT_TYPE("Deposit VA - Transaction History Report Type"),
    DEPOSIT_LINKED_VA_ACCOUNT_LISTING_RPT_TYPE("Deposit Linked VA - Account Listing Report Type"),
    /* Auto Finance */
    AF_HP_ACCOUNT_LISTING_RPT_TYPE("Auto Finance - Hire Purchase Account Listing Report Type"),
    AF_HP_TRX_HISTORY_RPT_TYPE("Auto Finance - Hire Purchase Transaction History Report Type"),

    AF_FS_ACCOUNT_LISTING_RPT_TYPE("Auto Finance Floor Stocking - Account Listing Report Type"),
    AF_FS_SUB_ACCOUNT_LISTING_RPT_TYPE("Auto Finance Floor Stocking - Sub Account Listing Report Type"),
    AF_FS_TRX_HISTORY_RPT_TYPE("Auto Finance Floor Stocking - Transaction History Report Type"),

    AF_BD_ACCOUNT_LISTING_RPT_TYPE("Auto Finance Block Discounting - Account Listing Report Type"),
    AF_BD_SUB_ACCOUNT_LISTING_RPT_TYPE("Auto Finance Block Discounting - Sub Account Listing Report Type"),
    AF_BD_TRX_HISTORY_RPT_TYPE("Auto Finance Block Discounting - Transaction History Report Type"),

    AF_PBR_ACCLISTING_RPT_TYPE("Autofinance PBR - Account Listing Report Type"),
    AF_PBR_SUBACCLISTING_RPT_TYPE("Autofinance PBR - Subaccount Listing Report Type"),
    AF_PBR_TRXHISTORIES_RPT_TYPE("Autofinance PBR - Transaction History Report Type"),

    /* Trade */
    TRADE_ACCOUNTLISTING_RPT_TYPE("Trade Financing - Account Listing Report Type"),

    /* Corporate Card */
    CREDIT_CARD_LISTING_RPT_TYPE("Corporate Card - Credit Card Listing Report Type"),
    CREDIT_CARD_TRXHISTORIES_RPT_TYPE("Corporate Card - Credit Card Transaction History Report Type"),
    CREDIT_CARD_PENDING_TRX_RPT_TYPE("Corporate Card - Credit Card Pending Transaction Report Type"),
    CREDIT_CARD_MASTER_LISTING_RPT_TYPE("Corporate Card - Credit Card Master Listing Report Type"),

    CHARGE_CARD_LISTING_RPT_TYPE("Corporate Card - Charge Card Listing Report Type"),
    CHARGE_CARD_TRXHISTORIES_RPT_TYPE("Corporate Card - Charge Card Transaction History Report Type"),
    CHARGE_CARD_PENDING_TRX_RPT_TYPE("Corporate Card - Charge Card Pending Report Type"),

    DEBIT_CARD_LISTING_RPT_TYPE("Corporate Card - Debit Card Listing Report Type"),

    /* Investment */
    UNIT_TRUST_ACCOUNTLISTING_RPT_TYPE("Unit Trust - Account Listing Report Type"),
    UNIT_TRUST_FUND_LISTING_RPT_TYPE("Unit Trust Fund - Fund Listing Report Type"),
    UNIT_TRUST_TRXHISTORIES_RPT_TYPE("Unit Trust - Transaction History Report Type"),

    /* Other Bank */
    OTHER_BANK_DEPOSIT_ACCOUNT_LISTING_RPT_TYPE("Other Bank - Deposit Account Listing Report Type"),
    OTHER_BANK_TRXHISTORIES_RPT_TYPE("Other Bank - Deposit Account Transaction History Report Type"),

    OTHER_BANK_LOAN_ACCOUNT_LISTING_RPT_TYPE("Other Bank - Loan Account Account Listing Report Type"),
    OTHER_BANK_LOAN_TRXHISTORIES_RPT_TYPE("Other Bank - Loan Account Transaction History Report Type"),

    /* Portfolio Overview */
    OVERVIEW_LISTING_RPT_TYPE("Portfolio Summary Report Type"),

    /* E-invoice */
    EINVOICE_LISTING_RPT_TYPE("E-invoice Listing Report Type");

    private final String description;
    ReportType(String description) {
        this.description = description;
    }
}
