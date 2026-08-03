package com.m2e.dashboard.model.BTS.Response.Sumary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class SummaryCategory {

    @JsonProperty("name")
    private String name;

    @JsonProperty("type")
    private String type;

    @JsonProperty("totalAmountDisp")
    private String totalAmountDisp;

    @JsonProperty("percValue")
    private String percValue;

    @JsonProperty("totalProduct")
    private String totalProduct;

    @JsonProperty("isMaintanence")
    private Boolean isMaintanence;

    @JsonProperty("maintanenceMsg")
    private String maintanenceMsg;

    @JsonProperty("productList")
    private List<SummaryProduct> productList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTotalAmountDisp() {
        return totalAmountDisp;
    }

    public void setTotalAmountDisp(String totalAmountDisp) {
        this.totalAmountDisp = totalAmountDisp;
    }

    public String getPercValue() {
        return percValue;
    }

    public void setPercValue(String percValue) {
        this.percValue = percValue;
    }

    public String getTotalProduct() {
        return totalProduct;
    }

    public void setTotalProduct(String totalProduct) {
        this.totalProduct = totalProduct;
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

    public List<SummaryProduct> getProductList() {
        return productList;
    }

    public void setProductList(List<SummaryProduct> productList) {
        this.productList = productList;
    }
}


