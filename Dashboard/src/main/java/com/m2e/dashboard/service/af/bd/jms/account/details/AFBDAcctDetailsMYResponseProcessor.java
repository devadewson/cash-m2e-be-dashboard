package com.m2e.dashboard.service.af.bd.jms.account.details;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.bd.AFBDMasterAcctDetailDTO;
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
@ESBServiceMapping(ESBServiceType.AF_BD_ACCT_DETAILS_MY)
public class AFBDAcctDetailsMYResponseProcessor implements ESBResponseProcessor<AFBDMasterAcctDetailDTO> {
    private final ObjectMapper objectMapper;
    private final ESBAFBDAcctDetailsMapper esbafbdAcctDetailsMapper;
    private final ESBMetadataService metadataService;

    @Autowired
    public AFBDAcctDetailsMYResponseProcessor(ObjectMapper objectMapper, ESBAFBDAcctDetailsMapper esbafbdAcctDetailsMapper, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbafbdAcctDetailsMapper = esbafbdAcctDetailsMapper;
        this.metadataService = metadataService;
    }

    @Override
    public AFBDMasterAcctDetailDTO processResponse(ESBDTO result, Object request) {
        AFBDMasterAcctDetailDTO afhpDetailDTO = new AFBDMasterAcctDetailDTO();
        ESBAFBDAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFBDAcctBalanceInquiryMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBAFBDAccountDTO> data = respMsgBody.getAccounts();
            if (data != null && !data.isEmpty()) {
                ESBAFBDAccountDTO esbafbdAccountDTO = data.get(0);
                afhpDetailDTO = esbafbdAcctDetailsMapper.mapToMasterDetailMYResp(esbafbdAccountDTO);
            }
        }
        // esb metadata injection
        metadataService.attachMetadata(result, afhpDetailDTO);
        return afhpDetailDTO;
    }
}
