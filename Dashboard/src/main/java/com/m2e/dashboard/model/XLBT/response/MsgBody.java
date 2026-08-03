package com.m2e.dashboard.model.XLBT.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MsgBody {
    @JsonProperty("LoanAccountListResponseData")
    private LoanAccountListResponseData loanAccountListResponseData ;
    @JsonProperty("OcissInfo")
    private List<String> ocissInfo;

    public LoanAccountListResponseData getLoanAccountListResponseData() {
        return loanAccountListResponseData;
    }

    public void setLoanAccountListResponseData(LoanAccountListResponseData loanAccountListResponseData) {
        this.loanAccountListResponseData = loanAccountListResponseData;
    }

    public List<String> getOcissInfo() {
        return ocissInfo;
    }

    public void setOcissInfo(List<String> ocissInfo) {
        this.ocissInfo = ocissInfo;
    }
}
