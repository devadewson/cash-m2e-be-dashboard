package com.m2e.dashboard.jms.af.fs.request.account.details;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ESBAFFSAcctInquiryMsgBodyReqDTO implements Serializable {
    @JsonProperty("AccountList")
    private List<ESBAcctListingReqDTO> accountListing;
}