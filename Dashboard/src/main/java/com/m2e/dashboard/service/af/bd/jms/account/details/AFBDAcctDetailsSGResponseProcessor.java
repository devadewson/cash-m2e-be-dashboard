package com.m2e.dashboard.service.af.bd.jms.account.details;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.bd.AFBDSGAcctDetailDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.bd.response.account.balanceinquiry.ESBAFBDAcctBalanceInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.bd.response.account.details.ESBAFBDAccountDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.bd.ESBAFBDAcctDetailsMapper;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ESBServiceMapping(ESBServiceType.AF_BD_MASTER_ACCT_DETAILS_SG)
public class AFBDAcctDetailsSGResponseProcessor implements ESBResponseProcessor<AFBDSGAcctDetailDTO> {
    private final ObjectMapper objectMapper;
    private final ESBAFBDAcctDetailsMapper esbafbdAcctDetailsMapper;
    private final ESBMetadataService metadataService;

    @Autowired
    public AFBDAcctDetailsSGResponseProcessor(ObjectMapper objectMapper, ESBAFBDAcctDetailsMapper esbafbdAcctDetailsMapper, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbafbdAcctDetailsMapper = esbafbdAcctDetailsMapper;
        this.metadataService = metadataService;
    }

    @Override
    public AFBDSGAcctDetailDTO processResponse(ESBDTO result, Object request) {
        AFBDSGAcctDetailDTO afhpDetailDTO = new AFBDSGAcctDetailDTO();
        ESBAFBDAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFBDAcctBalanceInquiryMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBAFBDAccountDTO> data = respMsgBody.getAccounts();
            if (data != null && !data.isEmpty()) {
                ESBAFBDAccountDTO esbafbdAccountDTO = data.get(0);
                afhpDetailDTO = esbafbdAcctDetailsMapper.mapToMasterDetailSGResp(esbafbdAccountDTO);
            }
        }
        // esb metadata injection
        metadataService.attachMetadata(result, afhpDetailDTO);
        return afhpDetailDTO;
    }
}
