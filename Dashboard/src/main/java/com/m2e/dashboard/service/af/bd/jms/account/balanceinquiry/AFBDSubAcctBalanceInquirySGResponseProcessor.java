package com.m2e.dashboard.service.af.bd.jms.account.balanceinquiry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.bd.AFBDSubAcctListSGDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.bd.response.account.balanceinquiry.ESBAFBDBlockInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.bd.response.account.details.ESBAFBDBlockDetailDTO;
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
@ESBServiceMapping(ESBServiceType.AF_BD_SUB_ACCT_BALANCE_INQUIRY_SG)
public class AFBDSubAcctBalanceInquirySGResponseProcessor implements ESBResponseProcessor<AFAcctListESBDTO> {
    private static final Logger logger = LoggerFactory.getLogger(AFBDSubAcctBalanceInquirySGResponseProcessor.class);

    private final ObjectMapper objectMapper;
    private final ESBAFBDAcctListInquiryMapper esbafbdAcctBalanceInquiryMapper;
    private final AsyncMappingService asyncMappingService;
    private final ESBMetadataService metadataService;

    @Autowired
    public AFBDSubAcctBalanceInquirySGResponseProcessor(ObjectMapper objectMapper, ESBAFBDAcctListInquiryMapper esbafbdAcctBalanceInquiryMapper,
                                                        AsyncMappingService asyncMappingService, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbafbdAcctBalanceInquiryMapper = esbafbdAcctBalanceInquiryMapper;
        this.asyncMappingService = asyncMappingService;
        this.metadataService = metadataService;
    }

    @Override
    public AFAcctListESBDTO processResponse(ESBDTO result, Object request) {
        AFAcctListESBDTO afbdAcctListESBDTO = new AFAcctListESBDTO();
        List<AFBDSubAcctListSGDTO> acctListViewAcctDTOS = new ArrayList<>();
        ESBAFBDBlockInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFBDBlockInquiryMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBAFBDBlockDetailDTO> datas = respMsgBody.getAccounts();
            if (datas != null && !datas.isEmpty()) {
                CompletableFuture<List<AFBDSubAcctListSGDTO>> futureResult = asyncMappingService.mapToListAsync(datas, esbafbdAcctBalanceInquiryMapper::mapToSubSummarySGResp);
                acctListViewAcctDTOS = futureResult.join();
                logger.info("account processor result: {}", acctListViewAcctDTOS);
            }
        }
        afbdAcctListESBDTO.setAcctListing(acctListViewAcctDTOS);
        // esb metadata injection
        metadataService.attachMetadata(result, afbdAcctListESBDTO);
        return afbdAcctListESBDTO;
    }
}
