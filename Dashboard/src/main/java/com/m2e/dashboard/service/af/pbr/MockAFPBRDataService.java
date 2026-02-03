package com.m2e.dashboard.service.af.pbr;

import com.m2e.dashboard.jms.af.pbr.request.account.ESBPBRAcctListingReqDTO;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MockAFPBRDataService {
    public List<ESBPBRAcctListingReqDTO> esbAcctBalanceInquiryReq() {
        return new ArrayList<>(Arrays.asList(
                ESBPBRAcctListingReqDTO.builder().accountNumber("412508020418").accountType("").currency("").region("").build()));
    }
}
