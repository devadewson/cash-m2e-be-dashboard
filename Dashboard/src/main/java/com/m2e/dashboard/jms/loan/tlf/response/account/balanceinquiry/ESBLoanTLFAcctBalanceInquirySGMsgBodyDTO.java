package com.m2e.dashboard.jms.loan.tlf.response.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFAccountSGDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBLoanTLFAcctBalanceInquirySGMsgBodyDTO implements Serializable {
    @JsonProperty("Account")
    private List<ESBLoanTLFAccountSGDTO> accounts;
}
