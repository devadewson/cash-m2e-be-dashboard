package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum ESBServiceType {
    //MY ESB Service
    DEPOSIT_CA_ACCT_BALANCE_INQUIRY_MY("Deposit Current Account Balance Inquiry - MY"),
    DEPOSIT_CA_ACCT_ACTIVITY_INQUIRY_MY("Deposit Current Account Activity Inquiry - MY"),
    DEPOSIT_CA_ACCT_DETAILS_MY("Deposit Current Account Details - MY"),
    DEPOSIT_CA_ACCT_STATS_MY("Deposit Current Account Statistics - MY"),
    DEPOSIT_CA_OMNIBUS_BALANCE_INQUIRY("Deposit Current Account Omnibus Balance Inquiry"),
    DEPOSIT_CA_ACCT_FLOAT_DETAILS("Deposit Current Account Float Details"),
    DEPOSIT_FD_ACCT_BALANCE_INQUIRY_MY("Deposit Fixed Deposit Balance Inquiry - MY"),
    DEPOSIT_FD_ACCT_DETAILS_MY("Deposit Fixed Deposit Account Details - MY"),
    DEPOSIT_FD_ACCT_STATS_MY("Deposit Fixed Account Statistics - MY"),
    DEPOSIT_FD_ACCT_CERT_DETAIL_INQUIRY_MY("Deposit Fixed Deposit Cert Details - MY"),
    DEPOSIT_CA_GPI_STATUS("Deposit Current Account GPI Status"),
    DEPOSIT_VA_ACCT_ACTIVITY_INQUIRY_MY("Deposit Virtual Account Activity Inquiry - MY"),

    //SG ESB Service
    DEPOSIT_CA_ACCT_BALANCE_INQUIRY_SG("Deposit Current Account Balance Inquiry - SG"),
    DEPOSIT_CA_ACCT_ACTIVITY_INQUIRY_SG("Deposit Current Account Activity Inquiry - SG"),
    DEPOSIT_CA_ACCT_DETAILS_SG("Deposit Current Account Details - SG"),
    DEPOSIT_CA_ACCT_STATS_SG("Deposit Current Account Statistics - SG"),
    DEPOSIT_FD_ACCT_BALANCE_INQUIRY_SG("Deposit Fixed Deposit Balance Inquiry - SG"),
    DEPOSIT_FD_ACCT_DETAILS_SG("Deposit Fixed Deposit Account Details - SG"),
    DEPOSIT_FD_ACCT_STATS_SG("Deposit Fixed Account Statistics - SG"),
    DEPOSIT_FD_ACCT_CERT_DETAIL_INQUIRY_SG("Deposit Fixed Deposit Cert Details - SG"),

    LOAN_TLF_ACCT_ACTIVITY_INQUIRY_MY_CONV("Loan Term Loan Financing Account Activity Inquiry - MY Conventional"),
    LOAN_TLF_ACCT_ACTIVITY_INQUIRY_MY_ISLAMIC("Loan Term Loan Financing Account Activity Inquiry - MY Islamic"),
    LOAN_TLF_ACCT_ACTIVITY_INQUIRY_MY_FOREIGN_CONV("Loan Term Loan Financing Account Activity Inquiry - Foreign Conventional"),
    LOAN_TLF_ACCT_ACTIVITY_INQUIRY_MY_FOREIGN_ISLAMIC("Loan Term Loan Financing Account Activity Inquiry - Foreign Islamic"),
    LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_CONV("Loan Term Loan Financing Account Balance Inquiry - MY Conventional"),
    LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_ISLAMIC("Loan Term Loan Financing Account Balance Inquiry - MY Islamic"),
    LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_FOREIGN_CONV("Loan Term Loan Financing Account Balance Inquiry - MY Foreign Conventional"),
    LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_FOREIGN_ISLAMIC("Loan Term Loan Financing Account Balance Inquiry - MY Foreign Islamic"),
    LOAN_TLF_ACCT_BALANCE_INQUIRY_SG_SIBS_CONV("Loan Term Loan Financing Account Balance Inquiry - SG SIBS Conventional"),
    LOAN_TLF_ACCT_BALANCE_INQUIRY_SG_SIBS_ISLAMIC("Loan Term Loan Financing Account Balance Inquiry - SG SIBS Islamic"),
    LOAN_TLF_ACCT_DETAILS_MY_CONV("Loan Term Loan Financing Account Details - MY Conventional"),
    LOAN_TLF_ACCT_DETAILS_MY_ISLAMIC("Loan Term Loan Financing Account Details - MY Islamic"),
    LOAN_TLF_ACCT_DETAILS_MY_FOREIGN_CONV("Loan Term Loan Financing Account Details - Foreign MY Conventional"),
    LOAN_TLF_ACCT_DETAILS_MY_FOREIGN_ISLAMIC("Loan Term Loan Financing Account Details - Foreign MY Islamic"),
    LOAN_TLF_ACCT_DETAILS_SG_SIBS_CONV("Loan Term Loan Financing Account Details - SG Conventional"),
    LOAN_TLF_ACCT_DETAILS_SG_SIBS_ISLAMIC("Loan Term Loan Financing Account Details - SG Islamic"),
    LOAN_TLF_TRANCHES_INQUIRY_FOREIGN_CONV("Loan Term Loan Financing Tranches Inquiry - Foreign Conventional"),
    LOAN_TLF_TRANCHES_INQUIRY_FOREIGN_ISLAMIC("Loan Term Loan Financing Tranches Inquiry - Foreign Islamic"),
    LOAN_TLF_TRANCH_DETAIL_INQUIRY_CONV("Loan Term Loan Financing Tranch Details - Foreign Conventional"),
    LOAN_TLF_TRANCH_DETAIL_INQUIRY_FOREIGN_ISLAMIC("Loan Term Loan Financing Tranch Details - Foreign Islamic"),

    AF_HP_ACCT_BALANCE_INQUIRY_MY("Auto Finance Hire Purchase Account Balance Inquiry - MY"),
    AF_BHP_ACCT_BALANCE_INQUIRY_MY("Auto Finance Business Hire Purchase Account Balance Inquiry - MY"),
    AF_HP_ACCT_BALANCE_INQUIRY_SG("Auto Finance Hire Purchase Account Balance Inquiry - SG"),
    AF_HP_ACCT_DETAILS_MY("Auto Finance Hire Purchase Account Details - MY"),
    AF_HP_MASTER_ACCT_DETAILS_MY("Auto Finance Hire Purchase Master Account Details - MY"),
    AF_HP_ACCT_ACTIVITY_INQUIRY_MY("Auto Finance Hire Purchase Account Activity Inquiry - MY"),
    AF_HP_ACCT_DETAILS_SG("Auto Finance Hire Purchase Account Details - SG"),

    AF_BD_ACCT_BALANCE_INQUIRY_MY("Auto Finance Block Discounting Account Balance Inquiry - MY"),
    AF_BD_MASTER_ACCT_BALANCE_INQUIRY_MY("Auto Finance Block Discounting Master Account Balance Inquiry - MY"),
    AF_BD_BLOCK_BALANCE_INQUIRY_MY("Auto Finance Block Discounting Block Balance Inquiry - MY"),
    AF_BD_AGREEMENT_BALANCE_INQUIRY_MY("Auto Finance Block Discounting Agreement Balance Inquiry - MY"),
    AF_BD_DEBIT_NOTE_BALANCE_INQUIRY_MY("Auto Finance Block Discounting Debit Note Balance Inquiry - MY"),
    AF_BD_ACCT_DETAILS_MY("Auto Finance Block Discounting Master Account Details - MY"),
    AF_BD_BLOCK_DETAILS_MY("Auto Finance Block Discounting Block Details - MY"),
    AF_BD_AGREEMENT_DETAILS_MY("Auto Finance Block Discounting Agreement Details - MY"),
    AF_BD_DEBIT_NOTE_DETAILS_MY("Auto Finance Block Discounting Debit Note Details - MY"),
    AF_BD_ACCT_ACTIVITY_INQUIRY_MY("Auto Finance Block Discounting Account Activity Inquiry - MY"),

    AF_BD_ACCT_BALANCE_INQUIRY_SG("Auto Finance Block Discounting Account Balance Inquiry - SG"),
    AF_BD_SUB_ACCT_BALANCE_INQUIRY_SG("Auto Finance Block Discounting Sub Account Inquiry - SG"),
    AF_BD_MASTER_ACCT_DETAILS_SG("Auto Finance Block Discounting Master Account Details - SG"),
    AF_BD_ACCT_DETAILS_SG("Auto Finance Block Discounting Master Account Details - SG"),

    AF_FS_MASTER_ACCT_DETAILS_MY("Auto Finance Floor Stocking Facility Account Details Inquiry - MY"),
    AF_FS_CERT_DETAILS_MY("Auto Finance Floor Stocking Cert Details Inquiry - MY"),
    AF_FS_ACCT_ACTIVITY_INQUIRY_MY("Auto Finance Floor Stocking Account Activity Inquiry - MY"),
    AF_FS_ACCT_DETAILS_SG("Auto Finance Floor Stocking Account Details Inquiry - SG"),

    AF_PBR_ACCT_BALANCE_INQUIRY_MY("Auto Finance Payment Before Registration Account Balance Inquiry"),
    AF_PBR_MASTER_BALANCE_INQUIRY_MY("Auto Finance Payment Before Registration Master Account Balance Inquiry - MY"),
    AF_PBR_UNIT_BALANCE_INQUIRY_MY("Auto Finance Payment Before Registration Unit Balance Inquiry - MY"),
    AF_PBR_ACCT_DETAILS_MY("Auto Finance Payment Before Registration Account Details Inquiry - MY"),
    AF_PBR_UNIT_DETAILS_MY("Auto Finance Payment Before Registration Unit Details Inquiry - MY"),
    AF_PBR_ACCT_ACTIVITY_INQUIRY_MY("Auto Finance Payment Before Registration Account Activity Inquiry - MY"),

    EINVOICE_VALIDATETIN_MY("e-Invoice Validate TIN - MY")
    ;


    private final String description;

    ESBServiceType(String description) {
        this.description = description;
    }
}
