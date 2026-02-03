package com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.af.bd.request.account.details.ESBAFBDBlockDetailsReqDTO;
import com.m2e.dashboard.jms.af.bd.response.account.details.ESBAFBDBlockDetailDTO;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ESBAFBDBlockInquiryMsgBodyReqDTO implements Serializable {
    @JsonProperty("AccountList")
    private List<ESBAFBDBlockDetailsReqDTO> accountListing;
}
