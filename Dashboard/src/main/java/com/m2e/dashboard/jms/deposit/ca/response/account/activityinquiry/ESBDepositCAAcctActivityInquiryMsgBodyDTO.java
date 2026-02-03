package com.m2e.dashboard.jms.deposit.ca.response.account.activityinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDTO;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBDepositCAAcctActivityInquiryMsgBodyDTO implements Serializable {
    @JsonProperty("MbbLastKey")
    private String mbbLastKey;
    @JsonProperty("AcctTrnRec")
    private List<ESBDepositCAAcctActivityInquiryTrnRecDTO> acctTrnRec;
}
