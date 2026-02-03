package com.m2e.dashboard.service.af.bd.jms.account.details;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.bd.AFBDSubAcctDetailDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.bd.response.account.balanceinquiry.ESBAFBDAgreementInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.bd.response.account.details.ESBAFBDAgreementDetailDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.bd.ESBAFBDAcctDetailsMapper;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ESBServiceMapping(ESBServiceType.AF_BD_AGREEMENT_DETAILS_MY)
public class AFBDAgreementDetailsMYResponseProcessor implements ESBResponseProcessor<AFBDSubAcctDetailDTO> {
    private final ObjectMapper objectMapper;
    private final ESBAFBDAcctDetailsMapper esbafbdAcctDetailsMapper;
    private final ESBMetadataService metadataService;

    @Autowired
    public AFBDAgreementDetailsMYResponseProcessor(ObjectMapper objectMapper, ESBAFBDAcctDetailsMapper esbafbdAcctDetailsMapper, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbafbdAcctDetailsMapper = esbafbdAcctDetailsMapper;
        this.metadataService = metadataService;
    }

    @Override
    public AFBDSubAcctDetailDTO processResponse(ESBDTO result, Object request) {
        AFBDSubAcctDetailDTO afbdDetailDTO = new AFBDSubAcctDetailDTO();
        ESBAFBDAgreementInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFBDAgreementInquiryMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBAFBDAgreementDetailDTO> data = respMsgBody.getAccounts();
            if (data != null && !data.isEmpty()) {
                ESBAFBDAgreementDetailDTO esbAFBDAccountDTO = data.get(0);
                afbdDetailDTO = esbafbdAcctDetailsMapper.mapToAgreementDetailMYResp(esbAFBDAccountDTO);
            }
        }
        // esb metadata injection
        metadataService.attachMetadata(result, afbdDetailDTO);
        return afbdDetailDTO;
    }
}
