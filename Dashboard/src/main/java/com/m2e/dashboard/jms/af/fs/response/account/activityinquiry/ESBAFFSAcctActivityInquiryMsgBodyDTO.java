package com.m2e.dashboard.jms.af.fs.response.account.activityinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBAFFSAcctActivityInquiryMsgBodyDTO implements Serializable {
    @JsonProperty("dbKey")
    private String mbbLastKey;
    @JsonProperty("AcctTrnRec")
    private List<ESBAFFSAcctActivityInquiryTrnRecDTO> acctTrnRec;
}