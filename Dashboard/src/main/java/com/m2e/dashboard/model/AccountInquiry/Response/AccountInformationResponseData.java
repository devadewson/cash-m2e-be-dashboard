package com.m2e.dashboard.model.AccountInquiry.Response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

public class AccountInformationResponseData {
    @JsonProperty("AccountNumber")
    private String accountNumber;

    @JsonProperty("AccountName")
    private String accountName;

    @JsonProperty("CIFNo")
    private String cifNo;

    @JsonProperty("AccountStatus")
    private String accountStatus;

    @JsonProperty("AccountStatusDesc")
    private String accountStatusDesc;

    @JsonProperty("Address1")
    private String address1;

    @JsonProperty("Address2")
    private String address2;

    @JsonProperty("Address3")
    private String address3;

    @JsonProperty("Address4")
    private String address4;

    @JsonProperty("AccountData")
    @JsonIgnoreProperties(ignoreUnknown = true)
    private AccountData accountData;
    @JsonProperty("ApplCode")
    private String applCode;

    @JsonProperty("ProductCode")
    private String productCode;

    @JsonProperty("DOB")
    private String dob;

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getCifNo() {
        return cifNo;
    }

    public void setCifNo(String cifNo) {
        this.cifNo = cifNo;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }

    public String getAccountStatusDesc() {
        return accountStatusDesc;
    }

    public void setAccountStatusDesc(String accountStatusDesc) {
        this.accountStatusDesc = accountStatusDesc;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getAddress3() {
        return address3;
    }

    public void setAddress3(String address3) {
        this.address3 = address3;
    }

    public String getAddress4() {
        return address4;
    }

    public void setAddress4(String address4) {
        this.address4 = address4;
    }

    public AccountData getAccountData() {
        return accountData;
    }

    public void setAccountData(AccountData accountData) {
        this.accountData = accountData;
    }

    public String getApplCode() {
        return applCode;
    }

    public void setApplCode(String applCode) {
        this.applCode = applCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }
}
