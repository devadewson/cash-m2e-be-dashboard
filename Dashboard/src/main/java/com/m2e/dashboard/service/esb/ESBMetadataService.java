package com.m2e.dashboard.service.esb;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.enums.ESBStatusCodeEnum;
import com.m2e.dashboard.enums.ErrorEnum;
import com.m2e.dashboard.jms.common.response.ESBAdditionalStatusCodesDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.jms.common.response.ESBMetadataDTO;
import com.m2e.dashboard.jms.common.response.ESBMsgHeaderDTO;
import com.m2e.dashboard.repository.ErrorMaintenanceRepository;
import com.m2e.dashboard.service.BaseESBMetadata;
import com.m2e.model.sso.base.ErrorMaintenance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ESBMetadataService {

    private static final Logger log = LoggerFactory.getLogger(ESBMetadataService.class);
    private final ErrorMaintenanceRepository errorMaintenanceRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    public ESBMetadataService(ErrorMaintenanceRepository errorMaintenanceRepository, ObjectMapper objectMapper) {
        this.errorMaintenanceRepository = errorMaintenanceRepository;
        this.objectMapper = objectMapper;
    }

    public <T extends BaseESBMetadata> T attachMetadata(ESBDTO result, T respObj) {
        ESBMsgHeaderDTO respMsgHeader = objectMapper.convertValue(result.getMessage().getMsgHeader(), ESBMsgHeaderDTO.class);
        ESBMetadataDTO esbMetadata = new ESBMetadataDTO();

        if (respMsgHeader == null) {
            log.warn("ESB returned null message header.");
            return attachDefaultMetadata(respObj, esbMetadata);
        }

        esbMetadata.setEsbStatusCode(respMsgHeader.getStatusCode());
        esbMetadata.setEsbStatusDesc(respMsgHeader.getStatusDesc());

        boolean isSuccess = ESBStatusCodeEnum.SUCCESS.getValue().equalsIgnoreCase(respMsgHeader.getStatusCode());
        esbMetadata.setIsESBError(!isSuccess);

        List<ESBAdditionalStatusCodesDTO> additionalStatusCodes = respMsgHeader.getAdditionalStatusCodes();
        if (additionalStatusCodes != null && !additionalStatusCodes.isEmpty()) {
            ESBAdditionalStatusCodesDTO status = additionalStatusCodes.get(0);
            esbMetadata.setHostStatusCode(status.getHostStatusCode());
            esbMetadata.setHostStatusDesc(status.getHostStatusDesc());

            if (!isSuccess) {
                enrichWithMaintenanceError(esbMetadata, status.getHostStatusCode());
            }
        } else {
            log.info("ESB returned no additional status code details.");
            if (!isSuccess) {
                attachDefaultErrorMetadata(esbMetadata);
            }
        }

        respObj.setEsbMetadata(esbMetadata);
        return respObj;

    }

    private <T extends BaseESBMetadata> T attachDefaultMetadata(T respObj, ESBMetadataDTO esbMetadata) {
        esbMetadata.setIsESBError(true);
        attachDefaultErrorMetadata(esbMetadata);
        respObj.setEsbMetadata(esbMetadata);
        return respObj;
    }

    public <T extends BaseESBMetadata> void attachTimeoutMetadata(T respObj) {
        ESBMetadataDTO esbMetadata = new ESBMetadataDTO();
        esbMetadata.setIsESBError(true);
        esbMetadata.setEsbStatusCode(ESBStatusCodeEnum.ESB_GATEWAY_TIMEOUT.getValue());
        esbMetadata.setEsbStatusDesc(ESBStatusCodeEnum.ESB_GATEWAY_TIMEOUT.name());
        respObj.setEsbMetadata(esbMetadata);
    }

    private void enrichWithMaintenanceError(ESBMetadataDTO metadata, String systemCode) {
        Optional<ErrorMaintenance> errorMt = errorMaintenanceRepository.findByErrorCategoryAndSystemCode(
                ErrorEnum.ERR_MT_HOST_CATEGORY.getValue(), systemCode);

        errorMt.ifPresentOrElse(
                err -> {
                    metadata.setMtStatusCode(err.getFeCode());
                    metadata.setMtStatusDesc(err.getFeMessage());
                },
                () -> attachDefaultErrorMetadata(metadata)
        );
    }

    private void attachDefaultErrorMetadata(ESBMetadataDTO metadata) {
        metadata.setMtStatusCode(ErrorEnum.ERR_ESB_DEFAULT_CD.getValue());
        metadata.setMtStatusDesc(ErrorEnum.ERR_ESB_DEFAULT_DESC.getValue());
    }
}
