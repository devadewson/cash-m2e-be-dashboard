package com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.af.bd.request.account.details.ESBAFBDDebitNoteDetailsReqDTO;
import com.m2e.dashboard.jms.af.bd.response.account.details.ESBAFBDDebitNoteDetailDTO;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ESBAFBDDebitNoteInquiryMsgBodyReqDTO implements Serializable {
    @JsonProperty("AccountList")
    private List<ESBAFBDDebitNoteDetailsReqDTO> accountListing;
}
