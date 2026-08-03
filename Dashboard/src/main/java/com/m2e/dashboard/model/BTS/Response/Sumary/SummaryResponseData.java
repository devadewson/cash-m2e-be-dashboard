package com.m2e.dashboard.model.BTS.Response.Sumary;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class SummaryResponseData {

    @JsonProperty("totalAssetsAmount")
    private String totalAssetsAmount;

    @JsonProperty("totalBorrowingsAmount")
    private String totalBorrowingsAmount;

    @JsonProperty("percAssetValue")
    private String percAssetValue;

    @JsonProperty("percBorrowingValue")
    private String percBorrowingValue;

    @JsonProperty("negTotalAssetFlag")
    private Boolean negTotalAssetFlag;

    @JsonProperty("posTotalBorrowingFlag")
    private Boolean posTotalBorrowingFlag;

    @JsonProperty("totalWealthAmount")
    private String totalWealthAmount;

    @JsonProperty("debitCreditIndicator")
    private Integer debitCreditIndicator;

    @JsonProperty("isMaintanence")
    private Boolean isMaintanence;

    @JsonProperty("maintanenceMsg")
    private String maintanenceMsg;

    @JsonProperty("assetList")
    private List<SummaryCategory> assetList;

    @JsonProperty("borrowingList")
    private List<SummaryCategory> borrowingList;

    @JsonProperty("exchangeRateList")
    private List<String> exchangeRateList;

    public String getTotalAssetsAmount() {
        return totalAssetsAmount;
    }

    public void setTotalAssetsAmount(String totalAssetsAmount) {
        this.totalAssetsAmount = totalAssetsAmount;
    }

    public String getTotalBorrowingsAmount() {
        return totalBorrowingsAmount;
    }

    public void setTotalBorrowingsAmount(String totalBorrowingsAmount) {
        this.totalBorrowingsAmount = totalBorrowingsAmount;
    }

    public String getPercAssetValue() {
        return percAssetValue;
    }

    public void setPercAssetValue(String percAssetValue) {
        this.percAssetValue = percAssetValue;
    }

    public String getPercBorrowingValue() {
        return percBorrowingValue;
    }

    public void setPercBorrowingValue(String percBorrowingValue) {
        this.percBorrowingValue = percBorrowingValue;
    }

    public Boolean getNegTotalAssetFlag() {
        return negTotalAssetFlag;
    }

    public void setNegTotalAssetFlag(Boolean negTotalAssetFlag) {
        this.negTotalAssetFlag = negTotalAssetFlag;
    }

    public Boolean getPosTotalBorrowingFlag() {
        return posTotalBorrowingFlag;
    }

    public void setPosTotalBorrowingFlag(Boolean posTotalBorrowingFlag) {
        this.posTotalBorrowingFlag = posTotalBorrowingFlag;
    }

    public String getTotalWealthAmount() {
        return totalWealthAmount;
    }

    public void setTotalWealthAmount(String totalWealthAmount) {
        this.totalWealthAmount = totalWealthAmount;
    }

    public Integer getDebitCreditIndicator() {
        return debitCreditIndicator;
    }

    public void setDebitCreditIndicator(Integer debitCreditIndicator) {
        this.debitCreditIndicator = debitCreditIndicator;
    }

    public Boolean getMaintanence() {
        return isMaintanence;
    }

    public void setMaintanence(Boolean maintanence) {
        isMaintanence = maintanence;
    }

    public String getMaintanenceMsg() {
        return maintanenceMsg;
    }

    public void setMaintanenceMsg(String maintanenceMsg) {
        this.maintanenceMsg = maintanenceMsg;
    }

    public List<SummaryCategory> getAssetList() {
        return assetList;
    }

    public void setAssetList(List<SummaryCategory> assetList) {
        this.assetList = assetList;
    }

    public List<SummaryCategory> getBorrowingList() {
        return borrowingList;
    }

    public void setBorrowingList(List<SummaryCategory> borrowingList) {
        this.borrowingList = borrowingList;
    }

    public List<String> getExchangeRateList() {
        return exchangeRateList;
    }

    public void setExchangeRateList(List<String> exchangeRateList) {
        this.exchangeRateList = exchangeRateList;
    }
}


