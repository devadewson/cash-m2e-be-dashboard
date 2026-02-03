package com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry.ESBSGAcctListingReqDTO;
import com.m2e.dashboard.jms.deposit.fd.request.account.certdetails.ESBSGDepositFDCertDetailsReqDTO;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class ESBSGDepositFDCertBalanceInquiryMsgBodyReqDTO implements Serializable {
    @JsonProperty("AccountList")
    private List<ESBSGDepositFDCertDetailsReqDTO> accountListing;
}
