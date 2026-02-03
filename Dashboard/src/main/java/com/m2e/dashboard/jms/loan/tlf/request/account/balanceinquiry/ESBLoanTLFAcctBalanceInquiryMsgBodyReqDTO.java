package com.m2e.dashboard.jms.loan.tlf.request.account.balanceinquiry;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import jakarta.annotation.security.DenyAll;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ESBLoanTLFAcctBalanceInquiryMsgBodyReqDTO implements Serializable {
    @JsonProperty("AccountList")
    private List<ESBAcctListingReqDTO> accountListing;
}
