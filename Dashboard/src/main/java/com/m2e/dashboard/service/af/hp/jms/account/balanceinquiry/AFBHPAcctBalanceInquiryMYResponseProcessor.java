package com.m2e.dashboard.service.af.hp.jms.account.balanceinquiry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.hp.AFHPAcctListDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.jms.af.hp.response.account.balanceinquiry.ESBAFHPAcctBalanceInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.hp.response.account.details.ESBAFHPAccountDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.hp.ESBAFHPAcctBalanceInquiryMapper;
import com.m2e.dashboard.service.AsyncMappingService;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@ESBServiceMapping(ESBServiceType.AF_BHP_ACCT_BALANCE_INQUIRY_MY)
public class AFBHPAcctBalanceInquiryMYResponseProcessor implements ESBResponseProcessor<AFAcctListESBDTO> {

    private final ObjectMapper objectMapper;
    private final ESBAFHPAcctBalanceInquiryMapper esbafhpAcctBalanceInquiryMapper;
    private final AsyncMappingService asyncMappingService;
    private final ESBMetadataService metadataService;

    @Autowired
    public AFBHPAcctBalanceInquiryMYResponseProcessor(ObjectMapper objectMapper, ESBAFHPAcctBalanceInquiryMapper esbafhpAcctBalanceInquiryMapper,
                                                      AsyncMappingService asyncMappingService, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbafhpAcctBalanceInquiryMapper = esbafhpAcctBalanceInquiryMapper;
        this.asyncMappingService = asyncMappingService;
        this.metadataService = metadataService;
    }

    @Override
    public AFAcctListESBDTO processResponse(ESBDTO result, Object request) {
        AFAcctListESBDTO afbdAcctListESBDTO = new AFAcctListESBDTO();
        List<AFHPAcctListDTO> afhpAcctListDTOList = new ArrayList<>();
        ESBAFHPAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFHPAcctBalanceInquiryMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBAFHPAccountDTO> datas = respMsgBody.getAccounts();
            if (datas != null && !datas.isEmpty()) {
                CompletableFuture<List<AFHPAcctListDTO>> futureResult = asyncMappingService.mapToListAsync(datas, esbafhpAcctBalanceInquiryMapper::mapToAFHPSummaryResp);
                afhpAcctListDTOList = futureResult.join();
            }
        }
        afbdAcctListESBDTO.setAcctListing(afhpAcctListDTOList);
        // esb metadata injection
        metadataService.attachMetadata(result, afbdAcctListESBDTO);
        return afbdAcctListESBDTO;
    }
}
