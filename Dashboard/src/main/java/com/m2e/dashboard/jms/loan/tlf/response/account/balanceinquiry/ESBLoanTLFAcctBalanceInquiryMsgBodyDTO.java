package com.m2e.dashboard.jms.loan.tlf.response.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFAccountDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBLoanTLFAcctBalanceInquiryMsgBodyDTO implements Serializable {
    @JsonProperty("Account")
    private List<ESBLoanTLFAccountDTO> accounts;
}
