package com.m2e.dashboard.model.AccountInquiry.Request;


import com.fasterxml.jackson.annotation.JsonProperty;

public class MsgBody {
    @JsonProperty("AccountNo")
    private String accountNo;
    @JsonProperty("AccountBranchCode")
    private String accountBranchCode;
    @JsonProperty("AccountCurrency")
    private String accountCurrency;

    public String getAccountNo() {
        return accountNo;
    }

    public void setAccountNo(String accountNo) {
        this.accountNo = accountNo;
    }

    public String getAccountBranchCode() {
        return accountBranchCode;
    }

    public void setAccountBranchCode(String accountBranchCode) {
        this.accountBranchCode = accountBranchCode;
    }

    public String getAccountCurrency() {
        return accountCurrency;
    }

    public void setAccountCurrency(String accountCurrency) {
        this.accountCurrency = accountCurrency;
    }
}
