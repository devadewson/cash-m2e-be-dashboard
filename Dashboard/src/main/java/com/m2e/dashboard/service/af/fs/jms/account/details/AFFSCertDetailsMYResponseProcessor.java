package com.m2e.dashboard.service.af.fs.jms.account.details;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.af.response.fs.AFFSCertDetailDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.jms.af.fs.response.account.balanceinquiry.ESBAFFSCertDetailsInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.af.fs.response.account.details.ESBAFFSCertDetailsDTO;
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
@ESBServiceMapping(ESBServiceType.AF_FS_CERT_DETAILS_MY)
public class AFFSCertDetailsMYResponseProcessor implements ESBResponseProcessor<AFAcctListESBDTO> {
    private static final Logger logger = LoggerFactory.getLogger(AFFSCertDetailsMYResponseProcessor.class);
    private final ObjectMapper objectMapper;
    private final ESBAFFSAcctDetailsMapper esbaffsAcctDetailsMapper;
    private final AsyncMappingService asyncMappingService;
    private final ESBMetadataService metadataService;

    @Autowired
    public AFFSCertDetailsMYResponseProcessor(ObjectMapper objectMapper, ESBAFFSAcctDetailsMapper esbaffsAcctDetailsMapper,
                                              AsyncMappingService asyncMappingService, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.asyncMappingService = asyncMappingService;
        this.esbaffsAcctDetailsMapper = esbaffsAcctDetailsMapper;
        this.metadataService = metadataService;
    }

    @Override
    public AFAcctListESBDTO processResponse(ESBDTO result, Object request) {
        AFAcctListESBDTO afbdAcctListESBDTO = new AFAcctListESBDTO();
        List<AFFSCertDetailDTO> affsDetailDTO = new ArrayList<>();
        ESBAFFSCertDetailsInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBAFFSCertDetailsInquiryMsgBodyDTO.class);
        if (respMsgBody==null){
            logger.error("Null ESB response msg for AFFSCertDetailsMY");
            return afbdAcctListESBDTO;
        }
        List<ESBAFFSCertDetailsDTO> data = respMsgBody.getAccounts();
        if (data != null && !data.isEmpty()) {
            CompletableFuture<List<AFFSCertDetailDTO>> futureResult = asyncMappingService.mapToListAsync(data, esbaffsAcctDetailsMapper::mapToCertDetailMYResp);
            affsDetailDTO = futureResult.join();
        }
        afbdAcctListESBDTO.setAcctListing(affsDetailDTO);
        // esb metadata injection
        metadataService.attachMetadata(result, afbdAcctListESBDTO);
        return afbdAcctListESBDTO;
    }
}
