package com.m2e.dashboard.service.af.bd.jms.account.details;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.bd.AFBDSGSubAcctDetailDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.bd.response.account.balanceinquiry.ESBAFBDSubAcctBalanceInquirySGMsgBodyDTO;
import com.m2e.dashboard.jms.af.bd.response.account.details.ESBAFBDSubAccountDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.bd.ESBAFBDAcctDetailsMapper;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ESBServiceMapping(ESBServiceType.AF_BD_ACCT_DETAILS_SG)
public class AFBDBlockDetailsSGResponseProcessor implements ESBResponseProcessor<AFBDSGSubAcctDetailDTO> {
    private final ObjectMapper objectMapper;
    private final ESBAFBDAcctDetailsMapper esbafbdAcctDetailsMapper;
    private final ESBMetadataService metadataService;

    @Autowired
    public AFBDBlockDetailsSGResponseProcessor(ObjectMapper objectMapper, ESBAFBDAcctDetailsMapper esbafbdAcctDetailsMapper, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbafbdAcctDetailsMapper = esbafbdAcctDetailsMapper;
        this.metadataService = metadataService;
    }

    @Override
    public AFBDSGSubAcctDetailDTO processResponse(ESBDTO result, Object request) {
        AFBDSGSubAcctDetailDTO afbdDetailDTO = new AFBDSGSubAcctDetailDTO();
        ESBAFBDSubAcctBalanceInquirySGMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFBDSubAcctBalanceInquirySGMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBAFBDSubAccountDTO> data = respMsgBody.getAccounts();
            if (data != null && !data.isEmpty()) {
                ESBAFBDSubAccountDTO esbAFBDAccountDTO = data.get(0);
                afbdDetailDTO = esbafbdAcctDetailsMapper.mapToSubDetailSGResp(esbAFBDAccountDTO);
            }
        }
        // esb metadata injection
        metadataService.attachMetadata(result, afbdDetailDTO);
        return afbdDetailDTO;
    }
}
