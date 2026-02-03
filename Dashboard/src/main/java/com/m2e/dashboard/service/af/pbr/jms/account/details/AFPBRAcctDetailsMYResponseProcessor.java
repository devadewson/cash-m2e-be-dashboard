package com.m2e.dashboard.service.af.pbr.jms.account.details;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRMasterAccountDetailDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.pbr.response.account.balanceinquiry.ESBAFPBRAcctBalanceInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.pbr.response.account.details.ESBAFPBRAccountDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.pbr.ESBAFPBRAcctDetailsMapper;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ESBServiceMapping(ESBServiceType.AF_PBR_ACCT_DETAILS_MY)
public class AFPBRAcctDetailsMYResponseProcessor implements ESBResponseProcessor<AFPBRMasterAccountDetailDTO> {
    private final ObjectMapper objectMapper;
    private final ESBAFPBRAcctDetailsMapper esbafpbrAcctDetailsMapper;
    private final ESBMetadataService metadataService;

    @Autowired
    public AFPBRAcctDetailsMYResponseProcessor(ObjectMapper objectMapper, ESBAFPBRAcctDetailsMapper esbafpbrAcctDetailsMapper, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbafpbrAcctDetailsMapper = esbafpbrAcctDetailsMapper;
        this.metadataService = metadataService;
    }

    @Override
    public AFPBRMasterAccountDetailDTO processResponse(ESBDTO result, Object request) {
        AFPBRMasterAccountDetailDTO afpbrMasterAccountDetailDTO = new AFPBRMasterAccountDetailDTO();
        ESBAFPBRAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFPBRAcctBalanceInquiryMsgBodyDTO.class);
        if (respMsgBody != null ) {
            List<ESBAFPBRAccountDTO> data = respMsgBody.getAccounts();
            if (data != null && !data.isEmpty()) {
                ESBAFPBRAccountDTO esbAFPBRAccountDTO = data.get(0);
                afpbrMasterAccountDetailDTO = esbafpbrAcctDetailsMapper.mapToAccountMasterDetailMYResp(esbAFPBRAccountDTO);
            }
        }

        metadataService.attachMetadata(result, afpbrMasterAccountDetailDTO);
        return afpbrMasterAccountDetailDTO;
    }
}
