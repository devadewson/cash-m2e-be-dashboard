package com.m2e.dashboard.jms.loan.tlf.response.account.activityinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ESBLoanTLFAcctActivityInquiryMsgBodyDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("MbbLastKey")
    private String mbbLastKey;
    @JsonProperty("AcctTrnRec")
    private List<ESBLoanTLFAcctActivityInquiryTrnRecDTO> acctTrnRec;
}
