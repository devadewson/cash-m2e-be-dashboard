package com.m2e.dashboard.service.af.bd.jms.debitnote.balanceinquiry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.bd.AFBDDebitNoteListDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.bd.response.account.balanceinquiry.ESBAFBDDebitNoteInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.bd.response.account.details.ESBAFBDDebitNoteDetailDTO;
import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.bd.ESBAFBDAcctListInquiryMapper;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@ESBServiceMapping(ESBServiceType.AF_BD_DEBIT_NOTE_BALANCE_INQUIRY_MY)
public class AFBDDebitNoteInquiryMYResponseProcessor implements ESBResponseProcessor<AFAcctListESBDTO> {

    private final ObjectMapper objectMapper;
    private final ESBAFBDAcctListInquiryMapper esbafbdAcctListInquiryMapper;
    private final ESBMetadataService metadataService;

    @Override
    public AFAcctListESBDTO processResponse(ESBDTO result, Object request) {
        AFAcctListESBDTO afbdAcctListESBDTO = new AFAcctListESBDTO();
        List<AFBDDebitNoteListDTO> afbdDebitNoteListDTOS = new ArrayList<>();
        ESBAFBDDebitNoteInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFBDDebitNoteInquiryMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBAFBDDebitNoteDetailDTO> datas = respMsgBody.getAccounts();
            if (datas != null && !datas.isEmpty()) {
                CompletableFuture<List<AFBDDebitNoteListDTO>> futureResult =
                        CompletableFuture.supplyAsync(() ->
                                datas.stream()
                                        .flatMap(parent -> esbafbdAcctListInquiryMapper.mapToDebitNoteListResp(parent).stream())
                                        .toList()
                        );
                afbdDebitNoteListDTOS = futureResult.join();
            }
        }
        afbdAcctListESBDTO.setAcctListing(afbdDebitNoteListDTOS);
        // esb metadata injection
        metadataService.attachMetadata(result, afbdAcctListESBDTO);
        return afbdAcctListESBDTO;
    }
}
