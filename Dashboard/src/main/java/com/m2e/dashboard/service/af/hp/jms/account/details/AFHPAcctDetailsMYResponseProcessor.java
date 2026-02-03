package com.m2e.dashboard.service.af.hp.jms.account.details;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.hp.AFHPAcctDetailDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.hp.response.account.balanceinquiry.ESBAFHPAcctBalanceInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.hp.response.account.details.ESBAFHPAccountDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.hp.ESBAFHPAcctDetailsMapper;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ESBServiceMapping(ESBServiceType.AF_HP_ACCT_DETAILS_MY)
public class AFHPAcctDetailsMYResponseProcessor implements ESBResponseProcessor<AFHPAcctDetailDTO> {
    private final ObjectMapper objectMapper;
    private final ESBAFHPAcctDetailsMapper esbafhpAcctDetailsMapper;
    private final ESBMetadataService esbMetadataService;

    @Autowired
    public AFHPAcctDetailsMYResponseProcessor(ObjectMapper objectMapper, ESBAFHPAcctDetailsMapper esbafhpAcctDetailsMapper, ESBMetadataService esbMetadataService) {
        this.objectMapper = objectMapper;
        this.esbafhpAcctDetailsMapper = esbafhpAcctDetailsMapper;
        this.esbMetadataService = esbMetadataService;
    }

    @Override
    public AFHPAcctDetailDTO processResponse(ESBDTO result, Object request) {
        AFHPAcctDetailDTO afhpDetailDTO = new AFHPAcctDetailDTO();
        ESBAFHPAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFHPAcctBalanceInquiryMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBAFHPAccountDTO> data = respMsgBody.getAccounts();
            if (data != null && !data.isEmpty()) {
                ESBAFHPAccountDTO esbAFHPAccountDTO = data.get(0);
                afhpDetailDTO = esbafhpAcctDetailsMapper.mapToAccountDetailMYResp(esbAFHPAccountDTO);
            }
        }

        esbMetadataService.attachMetadata(result, afhpDetailDTO);
        return afhpDetailDTO;
    }
}
