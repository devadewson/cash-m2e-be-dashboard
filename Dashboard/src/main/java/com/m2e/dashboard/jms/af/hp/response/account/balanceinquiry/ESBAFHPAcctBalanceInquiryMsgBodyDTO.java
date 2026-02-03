package com.m2e.dashboard.jms.af.hp.response.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.af.hp.response.account.details.ESBAFHPAccountDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBAFHPAcctBalanceInquiryMsgBodyDTO implements Serializable {
    @JsonProperty("Account")
    private List<ESBAFHPAccountDTO> accounts;
}
