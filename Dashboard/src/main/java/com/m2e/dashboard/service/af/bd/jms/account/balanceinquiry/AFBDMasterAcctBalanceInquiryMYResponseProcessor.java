package com.m2e.dashboard.service.af.bd.jms.account.balanceinquiry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.bd.AFBDAcctListViewFacilityDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.bd.response.account.balanceinquiry.ESBAFBDAcctBalanceInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.bd.response.account.details.ESBAFBDAccountDTO;
import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.bd.ESBAFBDAcctListInquiryMapper;
import com.m2e.dashboard.service.AsyncMappingService;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@ESBServiceMapping(ESBServiceType.AF_BD_MASTER_ACCT_BALANCE_INQUIRY_MY)
public class AFBDMasterAcctBalanceInquiryMYResponseProcessor implements ESBResponseProcessor<AFAcctListESBDTO> {
    private static final Logger logger = LoggerFactory.getLogger(AFBDMasterAcctBalanceInquiryMYResponseProcessor.class);

    private final ObjectMapper objectMapper;
    private final ESBAFBDAcctListInquiryMapper esbafbdAcctBalanceInquiryMapper;
    private final AsyncMappingService asyncMappingService;
    private final ESBMetadataService metadataService;

    @Autowired
    public AFBDMasterAcctBalanceInquiryMYResponseProcessor(ObjectMapper objectMapper, ESBAFBDAcctListInquiryMapper esbafbdAcctBalanceInquiryMapper,
                                                           AsyncMappingService asyncMappingService, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbafbdAcctBalanceInquiryMapper = esbafbdAcctBalanceInquiryMapper;
        this.asyncMappingService = asyncMappingService;
        this.metadataService = metadataService;
    }

    @Override
    public AFAcctListESBDTO processResponse(ESBDTO result, Object request) {
        AFAcctListESBDTO afbdAcctListESBDTO = new AFAcctListESBDTO();
        List<AFBDAcctListViewFacilityDTO> afbdAcctListViewFacilityDTOS = new ArrayList<>();
        ESBAFBDAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFBDAcctBalanceInquiryMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBAFBDAccountDTO> datas = respMsgBody.getAccounts();
            if (datas != null && !datas.isEmpty()) {
                CompletableFuture<List<AFBDAcctListViewFacilityDTO>> futureResult = asyncMappingService.mapToListAsync(datas, esbafbdAcctBalanceInquiryMapper::mapToMasterSummaryResp);
                afbdAcctListViewFacilityDTOS = futureResult.join();
                logger.info("facility processor result: {}", afbdAcctListViewFacilityDTOS);
            }
        }
        afbdAcctListESBDTO.setAcctListing(afbdAcctListViewFacilityDTOS);
        // esb metadata injection
        metadataService.attachMetadata(result, afbdAcctListESBDTO);
        return afbdAcctListESBDTO;
    }
}
