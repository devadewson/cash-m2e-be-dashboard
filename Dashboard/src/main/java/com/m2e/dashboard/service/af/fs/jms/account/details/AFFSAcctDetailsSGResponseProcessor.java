package com.m2e.dashboard.service.af.fs.jms.account.details;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.fs.AFFSSGAcctDetailDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.jms.af.fs.response.account.balanceinquiry.ESBAFFSAcctInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.fs.response.account.details.ESBAFFSAccountDetailsDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.mapper.af.fs.ESBAFFSAcctDetailsMapper;
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
@ESBServiceMapping(ESBServiceType.AF_FS_ACCT_DETAILS_SG)
public class AFFSAcctDetailsSGResponseProcessor implements ESBResponseProcessor<AFAcctListESBDTO> {
    private static final Logger logger = LoggerFactory.getLogger(AFFSAcctDetailsSGResponseProcessor.class);
    private final ObjectMapper objectMapper;
    private final ESBAFFSAcctDetailsMapper esbaffsAcctDetailsMapper;
    private final ESBMetadataService metadataService;
    private final AsyncMappingService asyncMappingService;

    @Autowired
    public AFFSAcctDetailsSGResponseProcessor(ObjectMapper objectMapper, ESBAFFSAcctDetailsMapper esbaffsAcctDetailsMapper,
                                              AsyncMappingService asyncMappingService, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbaffsAcctDetailsMapper = esbaffsAcctDetailsMapper;
        this.metadataService = metadataService;
        this.asyncMappingService = asyncMappingService;
    }

    @Override
    public AFAcctListESBDTO processResponse(ESBDTO result, Object request) {
        AFAcctListESBDTO afAcctListESBDTO = new AFAcctListESBDTO();
        List<AFFSSGAcctDetailDTO> affsDetailDTO = new ArrayList<>();
        ESBAFFSAcctInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFFSAcctInquiryMsgBodyDTO.class);
        if (respMsgBody==null){
            logger.error("Null ESB response msg for AFFSMasterAcctDetailsSG");
            return afAcctListESBDTO;
        }
        List<ESBAFFSAccountDetailsDTO> data = respMsgBody.getAccounts();
        if (data != null && !data.isEmpty()) {
            CompletableFuture<List<AFFSSGAcctDetailDTO>> futureResult = asyncMappingService.mapToListAsync(data, esbaffsAcctDetailsMapper::mapToAccountDetailSGResp);
            affsDetailDTO = futureResult.join();
        }
        afAcctListESBDTO.setAcctListing(affsDetailDTO);
        metadataService.attachMetadata(result, afAcctListESBDTO);
        return afAcctListESBDTO;
    }
}
