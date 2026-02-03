package com.m2e.dashboard.jms.deposit.ca.response.account.activityinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBDepositCAAcctActivityInquirySGMsgBodyDTO implements Serializable {
    @JsonProperty("MBB_LastKey")
    private String mbbLastKey;
    @JsonProperty("AcctTrnRec")
    private List<ESBDepositCAAcctActivityInquiryTrnRecDTO> acctTrnRec;
}