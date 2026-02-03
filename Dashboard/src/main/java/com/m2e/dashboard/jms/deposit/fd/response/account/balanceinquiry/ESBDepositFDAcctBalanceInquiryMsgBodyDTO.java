package com.m2e.dashboard.jms.deposit.fd.response.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBDepositFDAcctBalanceInquiryMsgBodyDTO implements Serializable {
    @JsonProperty("Account")
    private List<ESBDepositFDAccountDTO> accounts;
}
