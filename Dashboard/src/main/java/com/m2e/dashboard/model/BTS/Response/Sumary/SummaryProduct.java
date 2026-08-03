package com.m2e.dashboard.model.BTS.Response.Sumary;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryProduct {

    @JsonProperty("id")
    private String id;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("accNoDisp")
    private String accNoDisp;

    @JsonProperty("accNo")
    private String accNo;

    @JsonProperty("amountDisp")
    private String amountDisp;

    @JsonProperty("percentage")
    private String percentage;

    @JsonProperty("equivalentAmountDisp")
    private String equivalentAmountDisp;
    @JsonProperty("amount")
    private String amount;

    @JsonProperty("branchCode")
    private String branchCode;

    @JsonProperty("currencyCode")
    private String currencyCode;

    @JsonProperty("currencyCodeIso")
    private String currencyCodeIso;

    @JsonProperty("accountType")
    private String accountType;

    @JsonProperty("productType")
    private String productType;

    /**
     * BTS sometimes send:
     * null OR number
     */
    @JsonProperty("isIpo")
    private Integer isIpo;
    @JsonProperty("holdAmountDisp")
    private String holdAmountDisp;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getAccNoDisp() {
        return accNoDisp;
    }

    public void setAccNoDisp(String accNoDisp) {
        this.accNoDisp = accNoDisp;
    }

    public String getAccNo() {
        return accNo;
    }

    public void setAccNo(String accNo) {
        this.accNo = accNo;
    }

    public String getAmountDisp() {
        return amountDisp;
    }

    public void setAmountDisp(String amountDisp) {
        this.amountDisp = amountDisp;
    }

    public String getPercentage() {
        return percentage;
    }

    public void setPercentage(String percentage) {
        this.percentage = percentage;
    }

    public String getEquivalentAmountDisp() {
        return equivalentAmountDisp;
    }

    public void setEquivalentAmountDisp(String equivalentAmountDisp) {
        this.equivalentAmountDisp = equivalentAmountDisp;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public String getCurrencyCodeIso() {
        return currencyCodeIso;
    }

    public void setCurrencyCodeIso(String currencyCodeIso) {
        this.currencyCodeIso = currencyCodeIso;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public Integer getIsIpo() {
        return isIpo;
    }

    public void setIsIpo(Integer isIpo) {
        this.isIpo = isIpo;
    }

    public String getHoldAmountDisp() {
        return holdAmountDisp;
    }

    public void setHoldAmountDisp(String holdAmountDisp) {
        this.holdAmountDisp = holdAmountDisp;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }
}
