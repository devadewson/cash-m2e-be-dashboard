package com.m2e.dashboard.jms.deposit.ca.response.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBDepositCAAcctBalanceInquiryMsgBodyDTO implements Serializable {
    @JsonProperty("Account")
    private List<ESBDepositCAAccountDTO> accounts;
}
