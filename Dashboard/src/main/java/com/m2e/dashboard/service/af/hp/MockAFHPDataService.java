package com.m2e.dashboard.service.af.hp;

import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class MockAFHPDataService {
    public List<ESBAcctListingReqDTO> esbAcctBalanceInquiryReq() {
        return new ArrayList<>(Arrays.asList(
                ESBAcctListingReqDTO.builder().accountNumber("412508020418").accountType("").currency("").region("").build()));
    }
}
