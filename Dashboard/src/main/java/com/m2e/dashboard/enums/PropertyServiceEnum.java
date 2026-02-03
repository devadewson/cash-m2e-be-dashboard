package com.m2e.dashboard.enums;

import lombok.Getter;

@Getter
public enum PropertyServiceEnum {
    ESB("esb"),
    ESBMY("myQueue"),

    ESBMYV23("myv23Queue"),
    ESBSG("sgQueue"),

    /** Deposit CA MY **/
    DEPOSIT_CA_GETACCTBALANCEINQUIRY_MY("deposit-ca-get-acct-balance-inquiry-my"),
    DEPOSIT_CA_GETACCTACTIVITYINQUIRY_MY("deposit-ca-get-acct-activity-inquiry-my"),
    DEPOSIT_CA_GETACCTOMNIBUSINQUIRY("deposit-ca-get-acct-omnibus-inquiry"),
    DEPOSIT_CA_GETFLOATDETAILS("deposit-ca-get-float-details"),
    DEPOSIT_CA_GETSWIFTGPI("deposit-ca-get-swift-gpi"),

    /** Deposit CA SG **/
    DEPOSIT_CA_GETACCTBALANCEINQUIRY_SG("deposit-ca-get-acct-balance-inquiry-sg"),
    DEPOSIT_CA_GETACCTACTIVITYINQUIRY_SG("deposit-ca-get-acct-activity-inquiry-sg"),
    // SG ESB Omnibus not supported
    // SG ESB Float Detail not supported
    // SG ESB SWIFT GPI not supported

    /** Deposit FD MY **/
    DEPOSIT_FD_GETACCTBALANCEINQUIRY_MY("deposit-fd-get-acct-balance-inquiry-my"),
    DEPOSIT_FD_GETCERTDETAILS_SG("deposit-fd-get-cert-details-inquiry-sg"),

    /** Deposit FD SG **/
    DEPOSIT_FD_GETACCTBALANCEINQUIRY_SG("deposit-fd-get-acct-balance-inquiry-sg"),
    DEPOSIT_FD_GETCERTDETAILS_MY("deposit-fd-get-cert-details-inquiry-my"),

    LOAN_TLF_GETACCTACTIVITYINQUIRY_MY_CONV("loan-tlf-get-acct-activity-inquiry-my-conventional"),
    LOAN_TLF_GETACCTACTIVITYINQUIRY_MY_ISLAMIC("loan-tlf-get-acct-activity-inquiry-my-islamic"),
    LOAN_TLF_GETACCTACTIVITYINQUIRY_SG_SIBS_CONV("loan-tlf-get-acct-activity-inquiry-sg-conventional"),
    LOAN_TLF_GETACCTACTIVITYINQUIRY_SG_SIBS_ISLAMIC("loan-tlf-get-acct-activity-inquiry-sg-islamic"),
    LOAN_TLF_GETACCTACTIVITYINQUIRY_FOREIGN_MY_CONV("loan-tlf-get-acct-activity-inquiry-foreign-my-conventional"),
    LOAN_TLF_GETACCTACTIVITYINQUIRY_FOREIGN_MY_ISLAMIC("loan-tlf-get-acct-activity-inquiry-foreign-my-islamic"),
    LOAN_TLF_GETACCTDETAILS_MY_CONV("loan-tlf-get-acct-details-inquiry-my-conventional"),
    LOAN_TLF_GETACCTDETAILS_MY_ISLAMIC("loan-tlf-get-acct-details-inquiry-my-islamic"),
    LOAN_TLF_GETACCTDETAILS_FOREIGN_MY_CONV("loan-tlf-get-acct-details-inquiry-foreign-my-conventional"),
    LOAN_TLF_GETACCTDETAILS_FOREIGN_MY_ISLAMIC("loan-tlf-get-acct-details-inquiry-foreign-my-islamic"),
    LOAN_TLF_GETACCTDETAILS_SG_SIBS_CONV("loan-tlf-get-acct-details-inquiry-sg-sibs-conventional"),
    LOAN_TLF_GETACCTDETAILS_SG_SIBS_ISLAMIC("loan-tlf-get-acct-details-inquiry-sg-sibs-islamic"),
    LOAN_TLF_GETTRANCHDETAILS_FOREIGN_CONV("loan-tlf-get-tranch-details-conventional"),
    LOAN_TLF_GETTRANCHDETAILS_FOREIGN_ISLAMIC("loan-tlf-get-tranch-details-islamic"),

    AF_HP_GETACCTBALANCEINQUIRY_MY("autofinance-hp-get-acct-balance-inquiry-my"),
    AF_HP_GETMASTERACCTBALANCEINQUIRY_MY("autofinance-hp-get-facility-acct-balance-inquiry-my"),
    AF_HP_GETACCTBALANCEINQUIRY_SG("autofinance-hp-get-acct-balance-inquiry-sg"),
    AF_HP_GETACCTDETAILS_MY("autofinance-hp-get-acct-details-inquiry-my"),
    AF_HP_GETACCTACTIVITYINQUIRY_MY("autofinance-hp-get-acct-activity-inquiry-my"),
    AF_BHP_GETACCTBALANCEINQUIRY_MY("autofinance-bhp-get-acct-balance-inquiry-my"),
    AF_HP_GETACCTACTIVITYINQUIRY_SG("autofinance-hp-get-acct-activity-inquiry-sg"),
    AF_HP_GETACCTDETAILS_SG("autofinance-hp-get-acct-details-inquiry-sg"),

    AF_BD_GETACCTDETAILS_MY("autofinance-bd-get-acct-inquiry-my"),
    AF_BD_GETBLOCKDETAILS_MY("autofinance-bd-get-block-inquiry-my"),
    AF_BD_GETAGREEMENTDETAILS_MY("autofinance-bd-get-agreement-inquiry-my"),
    AF_BD_GETDEBITNOTEDETAILS_MY("autofinance-bd-get-debit-note-inquiry-my"),
    AF_BD_GETACCTACTIVITYINQUIRY_MY("autofinance-bd-get-acct-activity-inquiry-my"),
    AF_BD_GETACCTACTIVITYINQUIRY_SG("autofinance-bd-get-acct-activity-inquiry-sg"),
    AF_BD_GETACCTDETAILS_SG("autofinance-bd-get-acct-inquiry-sg"),
    AF_BD_GETSUBACCTDETAILS_SG("autofinance-bd-get-sub-acct-inquiry-sg"),

    AF_FS_GETMASTERACCTDETAILS_MY("autofinance-fs-get-acct-details-inquiry-my"),
    AF_FS_GETCERTDETAILS_MY("autofinance-fs-get-cert-details-inquiry-my"),
    AF_FS_GETACCTACTIVITYINQUIRY_MY("autofinance-fs-get-acct-activity-inquiry-my"),
    AF_FS_GETACCTACTIVITYINQUIRY_SG("autofinance-fs-get-acct-activity-inquiry-sg"),
    AF_FS_GETACCTDETAILS_SG("autofinance-fs-get-acct-details-inquiry-sg"),

    AF_PBR_GETACCTDETAILS_MY("autofinance-pbr-get-acct-details-inquiry-my"),
    AF_PBR_GETUNITDETAILS_MY("autofinance-pbr-get-unit-details-inquiry-my"),
    AF_PBR_GETACCTACTIVITYINQUIRY_MY("autofinance-pbr-get-acct-activity-inquiry-my"),

    EINVOICE_VALIDATETIN_MY("einvoice-validate-tin-my")
    ;

    private final String value;
    PropertyServiceEnum(String value) { this.value = value; }
}
