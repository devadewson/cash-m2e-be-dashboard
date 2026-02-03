package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum DownloadEnum {
    // LOAN
    LOAN_OD_ACCLISTING_FILENAME("loan_overdrafts_accountlisting_"),
    LOAN_RC_ACCLISTING_FILENAME("loan_revolving_credits_accountlisting_"),
    LOAN_RC_TRXHISTORIES_FILENAME("loan_rc_trx_histories_"),
    LOAN_RC_TRXHISTORIES_FITAS_FILENAME("loan_rc_trx_histories_fitas_"),
    LOAN_TLF_ACCLISTING_FILENAME("loan_tlf_accountlisting_"),
    LOAN_TLF_TRXHISTORIES_FILENAME("loan_tlf_trx_histories_"),
    LOAN_TLF_TRANCHES_FILENAME("loan_tlf_tranches_"),

    // CORPORATE CARD
    CREDIT_CARD_ACCLISTING_FILENAME("Credit Cards Account Balance "),
    CREDIT_CARD_TRXHISTORIES_FILENAME("Credit Cards Transaction History "),
    CREDIT_CARD_PENDING_TRX_FILENAME("credit_card_pending_trx_"), // TODO: double check the filename mapping
    CREDIT_CARD_MASTER_ACCLISTING_FILENAME("credit_card_master_accountlisting_"), // TODO: double check the filename mapping
    CHARGE_CARD_ACCLISTING_FILENAME("Charge Cards Account Balance "),
    CHARGE_CARD_TRXHISTORIES_FILENAME("Charge Cards Transaction History "),
    CHARGE_CARD_PENDING_TRX_FILENAME("charge_card_pending_trx_"), // TODO: double check the filename mapping
    DEBIT_CARD_ACCLISTING_FILENAME("Debit Cards Account Balance "),

    // DEPOSIT
    DEPOSIT_CA_ACCTLISTING_FILENAME("CA Account Balance "),
    DEPOSIT_CA_TRX_HISTORY_FILENAME("CA Transaction History "),
    DEPOSIT_CA_PAYMENT_HISTORY_FILENAME("CA Payment History "),
    DEPOSIT_CA_PAYMENT_DETAIL_FILENAME("CA Payment Details "),
    DEPOSIT_CA_EOD_BALANCE_FILENAME("CA EOD Balance"),
    DEPOSIT_CA_SWIFT_MT103_FILENAME("CA Swift MT103"),
    DEPOSIT_CA_GPI_STATUS_FILENAME("CA GPI Status"),
    DEPOSIT_FD_ACCLISTING_FILENAME("FD Account Balance "),
    DEPOSIT_TD_ACCLISTING_FILENAME("TD Account Balance "),
    DEPOSIT_FD_CERTLISTING_FILENAME("FD Deposits "),
    DEPOSIT_TD_CERTLISTING_FILENAME("TD Deposits "),
    DEPOSIT_FD_TRXHISTORIES_FILENAME("FD Transaction History "),
    DEPOSIT_TD_TRXHISTORIES_FILENAME("TD Transaction History "),

    DEPOSIT_VA_ACCTLISTING_FILENAME("VA Account List "),
    DEPOSIT_VA_TRXHISTORIES_FILENAME("VA Transaction History "),
    DEPOSIT_LINKED_VA_ACCT_LISTING_FILENAME("Linked VA Account List"),

    // INVESTMENT
    UNIT_TRUST_ACCLISTING_FILENAME("UT Account Balance "),
    UNIT_TRUST_FUND_LISTING_FILENAME("UT Funds "),
    UNIT_TRUST_FUND_TRXHISTORIES_FILENAME("UT Transaction History "),

    // TRADE
    TRADE_IMPORT_ACCLISTING_FILENAME("Import Account Balance "),
    TRADE_EXPORT_ACCLISTING_FILENAME("Export Account Balance "),
    TRADE_LOANS_ACCLISTING_FILENAME("Trade Loans Account Balance "),
    TRADE_GUARANTEE_ACCLISTING_FILENAME("Guarantee Account Balance "),

    // AF
    AF_HP_ACCOUNT_LISTING_FILENAME("HP BHP Account Balance "),
    AF_HP_TRX_HISTORY_FILENAME("HP BHP Transaction History "),
    AF_FS_ACCT_LISTING_FACILITY_VIEW_FILENAME("FS Facility Listing "),
    AF_FS_ACCT_LISTING_ACCT_VIEW_FILENAME("FS Account Listing"),
    AF_FS_SUB_ACCT_LISTING_FILENAME("FS Cert Listing"),
    AF_FS_TRX_HISTORY_FILENAME("FS Transaction History "),

    AF_BD_ACCT_LISTING_FILENAME("BD Account Balance "),
    AF_BD_SUB_ACCT_LISTING_FILENAME("BD Accounts "),
    AF_BD_TRX_HISTORY_FILENAME("BD Transaction History "),

    AF_PBR_ACCLISTING_FILENAME("PBR Account Balance "),
    AF_PBR_FACLISTING_FILENAME("PBR Facility Balance "),
    AF_PBR_TRXHISTORIES_FILENAME("PBR Transaction History "),
    AF_PBR_SUBACCLISTING_FILENAME("PBR Accounts "),
    // OTHER BANK
    OTHER_BANK_DEPOSIT_ACCTLISTING_FILENAME("OB Deposit Account Balance "),
    OTHER_BANK_DEPOSIT_TRXHISTORIES_FILENAME("OB Deposit Transaction History "),
    OTHER_BANK_LOAN_ACCTLISTING_FILENAME("OB Loan Financing Account Balance "),
    OTHER_BANK_LOAN_TRXHISTORIES_FILENAME("OB Loan Financing Transaction History "),

    // OVERVIEW
    OVERVIEW_FILENAME("Overview "),

    // E-INVOICE
    EINVOICE_LISTING_FILENAME("E-invoice Listing ")
    ;

    private final String description;

    DownloadEnum(String description) {
        this.description = description;
    }
}
