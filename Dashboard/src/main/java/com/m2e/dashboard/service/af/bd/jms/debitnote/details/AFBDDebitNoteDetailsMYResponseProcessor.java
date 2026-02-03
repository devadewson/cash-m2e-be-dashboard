package com.m2e.dashboard.service.af.bd.jms.debitnote.details;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.bd.AFBDDebitNoteDetailDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.bd.response.account.balanceinquiry.ESBAFBDDebitNoteInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.bd.response.account.details.ESBAFBDDebitNoteDetailDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.bd.ESBAFBDAcctDetailsMapper;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ESBServiceMapping(ESBServiceType.AF_BD_DEBIT_NOTE_DETAILS_MY)
public class AFBDDebitNoteDetailsMYResponseProcessor implements ESBResponseProcessor<AFBDDebitNoteDetailDTO> {
    private final ObjectMapper objectMapper;
    private final ESBAFBDAcctDetailsMapper esbafbdAcctDetailsMapper;
    private final ESBMetadataService esbMetadataService;

    @Autowired
    public AFBDDebitNoteDetailsMYResponseProcessor(ObjectMapper objectMapper, ESBAFBDAcctDetailsMapper esbafbdAcctDetailsMapper, ESBMetadataService esbMetadataService) {
        this.objectMapper = objectMapper;
        this.esbafbdAcctDetailsMapper = esbafbdAcctDetailsMapper;
        this.esbMetadataService = esbMetadataService;
    }

    @Override
    public AFBDDebitNoteDetailDTO processResponse(ESBDTO result, Object request) {
        AFBDDebitNoteDetailDTO afbdDebitNoteDetailDTO = new AFBDDebitNoteDetailDTO();
        ESBAFBDDebitNoteInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFBDDebitNoteInquiryMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBAFBDDebitNoteDetailDTO> data = respMsgBody.getAccounts();
            if (data != null && !data.isEmpty()) {
                ESBAFBDDebitNoteDetailDTO esbAFHPAccountDTO = data.get(0);
                afbdDebitNoteDetailDTO = esbafbdAcctDetailsMapper.mapToDebitNoteDetailMYResp(esbAFHPAccountDTO);
            }
        }
        esbMetadataService.attachMetadata(result, afbdDebitNoteDetailDTO);
        return afbdDebitNoteDetailDTO;
    }
}
