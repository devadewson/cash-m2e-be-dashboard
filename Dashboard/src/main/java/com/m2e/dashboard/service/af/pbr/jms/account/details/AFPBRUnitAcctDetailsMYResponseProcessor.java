package com.m2e.dashboard.service.af.pbr.jms.account.details;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRSubaccountDetailDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.pbr.response.account.balanceinquiry.ESBAFPBRUnitBalanceInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.pbr.response.account.details.ESBAFPBRUnitDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.pbr.ESBAFPBRAcctDetailsMapper;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ESBServiceMapping(ESBServiceType.AF_PBR_UNIT_DETAILS_MY)
public class AFPBRUnitAcctDetailsMYResponseProcessor implements ESBResponseProcessor<AFPBRSubaccountDetailDTO> {
    private final ObjectMapper objectMapper;
    private final ESBAFPBRAcctDetailsMapper esbafpbrAcctDetailsMapper;
    private final ESBMetadataService metadataService;

    @Autowired
    public AFPBRUnitAcctDetailsMYResponseProcessor(ObjectMapper objectMapper, ESBAFPBRAcctDetailsMapper esbafpbrAcctDetailsMapper, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbafpbrAcctDetailsMapper = esbafpbrAcctDetailsMapper;
        this.metadataService = metadataService;
    }

    @Override
    public AFPBRSubaccountDetailDTO processResponse(ESBDTO result, Object request) {
        AFPBRSubaccountDetailDTO afpbrSubaccountDetailDTO = new AFPBRSubaccountDetailDTO();
        ESBAFPBRUnitBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFPBRUnitBalanceInquiryMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBAFPBRUnitDTO> data = respMsgBody.getAccounts();
            if (data != null && !data.isEmpty()) {
                ESBAFPBRUnitDTO esbafpbrAccountDTO = data.get(0);
                afpbrSubaccountDetailDTO = esbafpbrAcctDetailsMapper.mapToUnitDetailMYResp(esbafpbrAccountDTO);
            }
        }

        metadataService.attachMetadata(result, afpbrSubaccountDetailDTO);
        return afpbrSubaccountDetailDTO;
    }
}
