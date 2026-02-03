package com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry.ESBSGAcctListingReqDTO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ESBSGDepositFDAcctBalanceInquiryMsgBodyReqDTO implements Serializable {
    @JsonProperty("AccountList")
    private List<ESBSGAcctListingReqDTO> accountListing;
}
