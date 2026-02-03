package com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.af.bd.request.account.details.ESBAFBDAgreementDetailsReqDTO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ESBAFBDAgreementInquiryMsgBodyReqDTO implements Serializable {
    @JsonProperty("AccountList")
    private List<ESBAFBDAgreementDetailsReqDTO> accountListing;
}
