package com.m2e.dashboard.service.af.bd.jms.account.details;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.bd.AFBDAcctDetailDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.bd.response.account.balanceinquiry.ESBAFBDBlockInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.bd.response.account.details.ESBAFBDBlockDetailDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.bd.ESBAFBDAcctDetailsMapper;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ESBServiceMapping(ESBServiceType.AF_BD_BLOCK_DETAILS_MY)
public class AFBDBlockDetailsMYResponseProcessor implements ESBResponseProcessor<AFBDAcctDetailDTO> {
    private final ObjectMapper objectMapper;
    private final ESBAFBDAcctDetailsMapper esbafbdAcctDetailsMapper;
    private final ESBMetadataService metadataService;

    @Autowired
    public AFBDBlockDetailsMYResponseProcessor(ObjectMapper objectMapper, ESBAFBDAcctDetailsMapper esbafbdAcctDetailsMapper, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbafbdAcctDetailsMapper = esbafbdAcctDetailsMapper;
        this.metadataService = metadataService;
    }

    @Override
    public AFBDAcctDetailDTO processResponse(ESBDTO result, Object request) {
        AFBDAcctDetailDTO afbdDetailDTO = new AFBDAcctDetailDTO();
        ESBAFBDBlockInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFBDBlockInquiryMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBAFBDBlockDetailDTO> data = respMsgBody.getAccounts();
            if (data != null && !data.isEmpty()) {
                ESBAFBDBlockDetailDTO esbAFBDAccountDTO = data.get(0);
                afbdDetailDTO = esbafbdAcctDetailsMapper.mapToBlockDetailMYResp(esbAFBDAccountDTO);
            }
        }
        // esb metadata injection
        metadataService.attachMetadata(result, afbdDetailDTO);
        return afbdDetailDTO;
    }
}
