package com.m2e.dashboard.jms.af.bd.response.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.af.bd.response.account.details.ESBAFBDSubAccountDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBAFBDSubAcctBalanceInquirySGMsgBodyDTO implements Serializable {
    @JsonProperty("Account")
    private List<ESBAFBDSubAccountDTO> accounts;
}
