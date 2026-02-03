package com.m2e.dashboard.jms.af.hp.request.account.activityinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.common.request.ESBDTRangeReqDTO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
@Builder
public class ESBAFHPAcctActivityInquiryMsgBodyReqDTO implements Serializable {
    @JsonProperty("AccountNo")
    private String accountNumber;
    @JsonProperty("AccountType")
    private String accountType;
    @JsonProperty("Currency")
    private String currency;
    @JsonProperty("MbbLastKey")
    private String mbbLastKey;
    @JsonProperty("DtRange")
    private ESBDTRangeReqDTO dtRange;
}
