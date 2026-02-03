package com.m2e.dashboard.jms.af.pbr.response.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.af.pbr.response.account.details.ESBAFPBRAccountDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBAFPBRAcctBalanceInquiryMsgBodyDTO implements Serializable {
    @JsonProperty("Account")
    private List<ESBAFPBRAccountDTO> accounts;
}
