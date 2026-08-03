package com.m2e.dashboard.model.XLBT.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class LoanAccountListResponseData {
    @JsonProperty("LoanAccount")

    private List<LoanAccount> loanAccounts;

    public List<LoanAccount> getLoanAccounts() {
        return loanAccounts;
    }

    public void setLoanAccounts(List<LoanAccount> loanAccounts) {
        this.loanAccounts = loanAccounts;
    }
}
