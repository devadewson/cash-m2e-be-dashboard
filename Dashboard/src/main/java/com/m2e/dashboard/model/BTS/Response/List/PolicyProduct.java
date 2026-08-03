package com.m2e.dashboard.model.BTS.Response.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyProduct {

    @JsonProperty("id")
    private String id;

    @JsonProperty("productName")
    private String productName;

    @JsonProperty("policyNo")
    private String policyNo;

    @JsonProperty("policyHolderName")
    private String policyHolderName;

    @JsonProperty("fundName")
    private String fundName;

    @JsonProperty("balanceUnitDisp")
    private String balanceUnitDisp;

    @JsonProperty("balanceDisp")
    private String balanceDisp;

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

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public String getPolicyHolderName() {
        return policyHolderName;
    }

    public void setPolicyHolderName(String policyHolderName) {
        this.policyHolderName = policyHolderName;
    }

    public String getFundName() {
        return fundName;
    }

    public void setFundName(String fundName) {
        this.fundName = fundName;
    }

    public String getBalanceUnitDisp() {
        return balanceUnitDisp;
    }

    public void setBalanceUnitDisp(String balanceUnitDisp) {
        this.balanceUnitDisp = balanceUnitDisp;
    }

    public String getBalanceDisp() {
        return balanceDisp;
    }

    public void setBalanceDisp(String balanceDisp) {
        this.balanceDisp = balanceDisp;
    }
}