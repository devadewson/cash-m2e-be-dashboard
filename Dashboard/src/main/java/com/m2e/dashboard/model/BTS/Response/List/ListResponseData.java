package com.m2e.dashboard.model.BTS.Response.List;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ListResponseData {
        @JsonProperty("totalValue")
        private String totalValue;

        @JsonProperty("totalProduct")
        private String totalProduct;

        @JsonProperty("productList")
        private List<PolicyProduct> productList;
        /**
         * BTS may return null OR list
         */
        @JsonProperty("exchangeRateList")
        private List<String> exchangeRateList;

    public String getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(String totalValue) {
        this.totalValue = totalValue;
    }

    public String getTotalProduct() {
        return totalProduct;
    }

    public void setTotalProduct(String totalProduct) {
        this.totalProduct = totalProduct;
    }

    public List<PolicyProduct> getProductList() {
        return productList;
    }

    public void setProductList(List<PolicyProduct> productList) {
        this.productList = productList;
    }

    public List<String> getExchangeRateList() {
        return exchangeRateList;
    }

    public void setExchangeRateList(List<String> exchangeRateList) {
        this.exchangeRateList = exchangeRateList;
    }
}

