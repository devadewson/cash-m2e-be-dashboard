package com.m2e.dashboard.service.af.pbr.jms.account.balanceinquiry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRUnitSummaryDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.jms.af.pbr.response.account.balanceinquiry.ESBAFPBRUnitBalanceInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.pbr.response.account.details.ESBAFPBRUnitDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.pbr.ESBAFPBRAcctBalanceInquiryMapper;
import com.m2e.dashboard.service.AsyncMappingService;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@ESBServiceMapping(ESBServiceType.AF_PBR_UNIT_BALANCE_INQUIRY_MY)
public class AFPBRUnitBalanceInquiryMYResponseProcessor implements ESBResponseProcessor<AFAcctListESBDTO> {

    private final ObjectMapper objectMapper;
    private final ESBAFPBRAcctBalanceInquiryMapper esbafpbrAcctBalanceInquiryMapper;
    private final AsyncMappingService asyncMappingService;
    private final ESBMetadataService metadataService;

    @Autowired
    public AFPBRUnitBalanceInquiryMYResponseProcessor(ObjectMapper objectMapper, ESBAFPBRAcctBalanceInquiryMapper esbafpbrAcctBalanceInquiryMapper,
                                                      AsyncMappingService asyncMappingService, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbafpbrAcctBalanceInquiryMapper = esbafpbrAcctBalanceInquiryMapper;
        this.asyncMappingService = asyncMappingService;
        this.metadataService = metadataService;
    }

    @Override
    public AFAcctListESBDTO processResponse(ESBDTO result, Object request) {
        AFAcctListESBDTO afbdAcctListESBDTO = new AFAcctListESBDTO();
        List<AFPBRUnitSummaryDTO> afpbrUnitSummaryDTOS = new ArrayList<>();
        ESBAFPBRUnitBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFPBRUnitBalanceInquiryMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBAFPBRUnitDTO> datas = respMsgBody.getAccounts();
            if (datas != null && !datas.isEmpty()) {
                CompletableFuture<List<AFPBRUnitSummaryDTO>> futureResult = asyncMappingService.mapToListAsync(datas, esbafpbrAcctBalanceInquiryMapper::mapToUnitSummaryResp);
                afpbrUnitSummaryDTOS = futureResult.join();
            }
        }
        afbdAcctListESBDTO.setAcctListing(afpbrUnitSummaryDTOS);
        // esb metadata injection
        metadataService.attachMetadata(result, afbdAcctListESBDTO);
        return afbdAcctListESBDTO;
    }
}
