package com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.af.bd.request.account.details.ESBAFBDSubAcctDetailsSGReqDTO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ESBAFBDSubAcctBalanceInquirySGMsgBodyReqDTO implements Serializable {
    @JsonProperty("AccountList")
    private List<ESBAFBDSubAcctDetailsSGReqDTO> accountListing;
}
