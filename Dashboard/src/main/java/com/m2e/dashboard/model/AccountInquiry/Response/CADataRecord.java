package com.m2e.dashboard.model.AccountInquiry.Response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class CADataRecord {
    @JsonProperty("TransId")
    private String transId;

    @JsonProperty("TransCode")
    private String transCode;

    @JsonProperty("AcctCcy")
    private String acctCcy;

    @JsonProperty("AcctBranch")
    private String acctBranch;

    @JsonProperty("AcctCtl4")
    private String acctCtl4;

    @JsonProperty("AcctNumber")
    private String acctNumber;

    @JsonProperty("Filler")
    private String filler;

    @JsonProperty("ResponseCode")
    private String responseCode;

    @JsonProperty("BalanceSign")
    private String balanceSign;

    @JsonProperty("DdaBalanceAmount")
    private String ddaBalanceAmount;

    @JsonProperty("AvailableBalance")
    private String availableBalance;

    @JsonProperty("HoldAmount")
    private String holdAmount;

    @JsonProperty("TodayClearing")
    private String todayClearing;

    @JsonProperty("YesterdayClearing")
    private String yesterdayClearing;

    @JsonProperty("OverDraftLimit")
    private String overDraftLimit;

    @JsonProperty("OverDraftInterest")
    private String overDraftInterest;

    @JsonProperty("OverDraftExpiryDate")
    private String overDraftExpiryDate;

    @JsonProperty("AccruedInterest")
    private String accruedInterest;

    @JsonProperty("BeginningBalance")
    private String beginningBalance;

    @JsonProperty("EndingBalance")
    private String endingBalance;

    @JsonProperty("CustomerName")
    private String customerName;

    @JsonProperty("MailAddrLine1")
    private String mailAddrLine1;

    @JsonProperty("MailAddrLine2")
    private String mailAddrLine2;

    @JsonProperty("MailAddrLine3")
    private String mailAddrLine3;

    @JsonProperty("MailAddrLine4")
    private String mailAddrLine4;

    @JsonProperty("CityState")
    private String cityState;

    @JsonProperty("ZipCode")
    private String zipCode;

    @JsonProperty("ProductCode")
    private String productCode;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("StatusDescription")
    private String statusDescription;

    @JsonProperty("OpenDate")
    private String openDate;

    @JsonProperty("LastMaintenanceDate")
    private String lastMaintenanceDate;

    @JsonProperty("HomePhone")
    private String homePhone;

    @JsonProperty("BusinessPhone")
    private String businessPhone;

    @JsonProperty("ODLimit2")
    private String odLimit2;

    @JsonProperty("ATMFlag")
    private String atmFlag;

    @JsonProperty("ResidentFlag")
    private String residentFlag;

    @JsonProperty("CleanupStatus")
    private String cleanupStatus;

    @JsonProperty("YesterdayClrStatus")
    private String yesterdayClrStatus;

    @JsonProperty("CIFNo")
    private String cifNo;

    @JsonProperty("MinimumBalance")
    private String minimumBalance;

    @JsonProperty("ProductName")
    private String productName;

    @JsonProperty("ShortAccountName")
    private String shortAccountName;

    @JsonProperty("TaxId")
    private String taxId;

    @JsonProperty("AccountType")
    private String accountType;

    @JsonProperty("IslamicFlag")
    private String islamicFlag;

    @JsonProperty("PreferredStmtLanguage")
    private String preferredStmtLanguage;

    @JsonProperty("CIFName")
    private String cifName;

    @JsonProperty("UtilisedAmount")
    private String utilisedAmount;

    @JsonProperty("UnutilisedAmount")
    private String unutilisedAmount;

    @JsonProperty("LedgerAmount")
    private String ledgerAmount;

    @JsonProperty("BusinessReg")
    private String businessReg;

    @JsonProperty("ForexAcumAmt")
    private String forexAcumAmt;

    @JsonProperty("Cabang")
    private String cabang;

    @JsonProperty("NPKCrossSeller")
    private String npkCrossSeller;

    @JsonProperty("Unused")
    private String unused;

    @JsonProperty("CardNumber")
    private String cardNumber;

    @JsonProperty("TranInAmount")
    private String tranInAmount;

    @JsonProperty("TranOutAmount")
    private String tranOutAmount;

    @JsonProperty("TranInFrequency")
    private String tranInFrequency;

    @JsonProperty("TranOutFrequency")
    private String tranOutFrequency;

    @JsonProperty("SourceOfFund")
    private String sourceOfFund;

    @JsonProperty("PurposeOfFund")
    private String purposeOfFund;

    public String getTransId() {
        return transId;
    }

    public void setTransId(String transId) {
        this.transId = transId;
    }

    public String getTransCode() {
        return transCode;
    }

    public void setTransCode(String transCode) {
        this.transCode = transCode;
    }

    public String getAcctCcy() {
        return acctCcy;
    }

    public void setAcctCcy(String acctCcy) {
        this.acctCcy = acctCcy;
    }

    public String getAcctBranch() {
        return acctBranch;
    }

    public void setAcctBranch(String acctBranch) {
        this.acctBranch = acctBranch;
    }

    public String getAcctCtl4() {
        return acctCtl4;
    }

    public void setAcctCtl4(String acctCtl4) {
        this.acctCtl4 = acctCtl4;
    }

    public String getAcctNumber() {
        return acctNumber;
    }

    public void setAcctNumber(String acctNumber) {
        this.acctNumber = acctNumber;
    }

    public String getFiller() {
        return filler;
    }

    public void setFiller(String filler) {
        this.filler = filler;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    public String getBalanceSign() {
        return balanceSign;
    }

    public void setBalanceSign(String balanceSign) {
        this.balanceSign = balanceSign;
    }

    public String getDdaBalanceAmount() {
        return ddaBalanceAmount;
    }

    public void setDdaBalanceAmount(String ddaBalanceAmount) {
        this.ddaBalanceAmount = ddaBalanceAmount;
    }

    public String getAvailableBalance() {
        return availableBalance;
    }

    public void setAvailableBalance(String availableBalance) {
        this.availableBalance = availableBalance;
    }

    public String getHoldAmount() {
        return holdAmount;
    }

    public void setHoldAmount(String holdAmount) {
        this.holdAmount = holdAmount;
    }

    public String getTodayClearing() {
        return todayClearing;
    }

    public void setTodayClearing(String todayClearing) {
        this.todayClearing = todayClearing;
    }

    public String getYesterdayClearing() {
        return yesterdayClearing;
    }

    public void setYesterdayClearing(String yesterdayClearing) {
        this.yesterdayClearing = yesterdayClearing;
    }

    public String getOverDraftLimit() {
        return overDraftLimit;
    }

    public void setOverDraftLimit(String overDraftLimit) {
        this.overDraftLimit = overDraftLimit;
    }

    public String getOverDraftInterest() {
        return overDraftInterest;
    }

    public void setOverDraftInterest(String overDraftInterest) {
        this.overDraftInterest = overDraftInterest;
    }

    public String getOverDraftExpiryDate() {
        return overDraftExpiryDate;
    }

    public void setOverDraftExpiryDate(String overDraftExpiryDate) {
        this.overDraftExpiryDate = overDraftExpiryDate;
    }

    public String getAccruedInterest() {
        return accruedInterest;
    }

    public void setAccruedInterest(String accruedInterest) {
        this.accruedInterest = accruedInterest;
    }

    public String getBeginningBalance() {
        return beginningBalance;
    }

    public void setBeginningBalance(String beginningBalance) {
        this.beginningBalance = beginningBalance;
    }

    public String getEndingBalance() {
        return endingBalance;
    }

    public void setEndingBalance(String endingBalance) {
        this.endingBalance = endingBalance;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getMailAddrLine1() {
        return mailAddrLine1;
    }

    public void setMailAddrLine1(String mailAddrLine1) {
        this.mailAddrLine1 = mailAddrLine1;
    }

    public String getMailAddrLine2() {
        return mailAddrLine2;
    }

    public void setMailAddrLine2(String mailAddrLine2) {
        this.mailAddrLine2 = mailAddrLine2;
    }

    public String getMailAddrLine3() {
        return mailAddrLine3;
    }

    public void setMailAddrLine3(String mailAddrLine3) {
        this.mailAddrLine3 = mailAddrLine3;
    }

    public String getMailAddrLine4() {
        return mailAddrLine4;
    }

    public void setMailAddrLine4(String mailAddrLine4) {
        this.mailAddrLine4 = mailAddrLine4;
    }

    public String getCityState() {
        return cityState;
    }

    public void setCityState(String cityState) {
        this.cityState = cityState;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDescription() {
        return statusDescription;
    }

    public void setStatusDescription(String statusDescription) {
        this.statusDescription = statusDescription;
    }

    public String getOpenDate() {
        return openDate;
    }

    public void setOpenDate(String openDate) {
        this.openDate = openDate;
    }

    public String getLastMaintenanceDate() {
        return lastMaintenanceDate;
    }

    public void setLastMaintenanceDate(String lastMaintenanceDate) {
        this.lastMaintenanceDate = lastMaintenanceDate;
    }

    public String getHomePhone() {
        return homePhone;
    }

    public void setHomePhone(String homePhone) {
        this.homePhone = homePhone;
    }

    public String getBusinessPhone() {
        return businessPhone;
    }

    public void setBusinessPhone(String businessPhone) {
        this.businessPhone = businessPhone;
    }

    public String getOdLimit2() {
        return odLimit2;
    }

    public void setOdLimit2(String odLimit2) {
        this.odLimit2 = odLimit2;
    }

    public String getAtmFlag() {
        return atmFlag;
    }

    public void setAtmFlag(String atmFlag) {
        this.atmFlag = atmFlag;
    }

    public String getResidentFlag() {
        return residentFlag;
    }

    public void setResidentFlag(String residentFlag) {
        this.residentFlag = residentFlag;
    }

    public String getCleanupStatus() {
        return cleanupStatus;
    }

    public void setCleanupStatus(String cleanupStatus) {
        this.cleanupStatus = cleanupStatus;
    }

    public String getYesterdayClrStatus() {
        return yesterdayClrStatus;
    }

    public void setYesterdayClrStatus(String yesterdayClrStatus) {
        this.yesterdayClrStatus = yesterdayClrStatus;
    }

    public String getCifNo() {
        return cifNo;
    }

    public void setCifNo(String cifNo) {
        this.cifNo = cifNo;
    }

    public String getMinimumBalance() {
        return minimumBalance;
    }

    public void setMinimumBalance(String minimumBalance) {
        this.minimumBalance = minimumBalance;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getShortAccountName() {
        return shortAccountName;
    }

    public void setShortAccountName(String shortAccountName) {
        this.shortAccountName = shortAccountName;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getIslamicFlag() {
        return islamicFlag;
    }

    public void setIslamicFlag(String islamicFlag) {
        this.islamicFlag = islamicFlag;
    }

    public String getPreferredStmtLanguage() {
        return preferredStmtLanguage;
    }

    public void setPreferredStmtLanguage(String preferredStmtLanguage) {
        this.preferredStmtLanguage = preferredStmtLanguage;
    }

    public String getCifName() {
        return cifName;
    }

    public void setCifName(String cifName) {
        this.cifName = cifName;
    }

    public String getUtilisedAmount() {
        return utilisedAmount;
    }

    public void setUtilisedAmount(String utilisedAmount) {
        this.utilisedAmount = utilisedAmount;
    }

    public String getUnutilisedAmount() {
        return unutilisedAmount;
    }

    public void setUnutilisedAmount(String unutilisedAmount) {
        this.unutilisedAmount = unutilisedAmount;
    }

    public String getLedgerAmount() {
        return ledgerAmount;
    }

    public void setLedgerAmount(String ledgerAmount) {
        this.ledgerAmount = ledgerAmount;
    }

    public String getBusinessReg() {
        return businessReg;
    }

    public void setBusinessReg(String businessReg) {
        this.businessReg = businessReg;
    }

    public String getForexAcumAmt() {
        return forexAcumAmt;
    }

    public void setForexAcumAmt(String forexAcumAmt) {
        this.forexAcumAmt = forexAcumAmt;
    }

    public String getCabang() {
        return cabang;
    }

    public void setCabang(String cabang) {
        this.cabang = cabang;
    }

    public String getNpkCrossSeller() {
        return npkCrossSeller;
    }

    public void setNpkCrossSeller(String npkCrossSeller) {
        this.npkCrossSeller = npkCrossSeller;
    }

    public String getUnused() {
        return unused;
    }

    public void setUnused(String unused) {
        this.unused = unused;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getTranInAmount() {
        return tranInAmount;
    }

    public void setTranInAmount(String tranInAmount) {
        this.tranInAmount = tranInAmount;
    }

    public String getTranOutAmount() {
        return tranOutAmount;
    }

    public void setTranOutAmount(String tranOutAmount) {
        this.tranOutAmount = tranOutAmount;
    }

    public String getTranInFrequency() {
        return tranInFrequency;
    }

    public void setTranInFrequency(String tranInFrequency) {
        this.tranInFrequency = tranInFrequency;
    }

    public String getTranOutFrequency() {
        return tranOutFrequency;
    }

    public void setTranOutFrequency(String tranOutFrequency) {
        this.tranOutFrequency = tranOutFrequency;
    }

    public String getSourceOfFund() {
        return sourceOfFund;
    }

    public void setSourceOfFund(String sourceOfFund) {
        this.sourceOfFund = sourceOfFund;
    }

    public String getPurposeOfFund() {
        return purposeOfFund;
    }

    public void setPurposeOfFund(String purposeOfFund) {
        this.purposeOfFund = purposeOfFund;
    }
}
