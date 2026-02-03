package com.m2e.dashboard.service.af.fs.jms;

import com.m2e.dashboard.dto.af.response.fs.*;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.common.AFAcctListESBDTO;
import com.m2e.dashboard.jms.af.fs.request.account.activityinquiry.ESBAFFSAcctActivityInquiryReqDTO;
import com.m2e.dashboard.jms.af.fs.request.account.details.ESBAFFSCertDetailsReqDTO;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import com.m2e.dashboard.repository.AccountStatusRepository;
import com.m2e.dashboard.service.esb.ESBClientService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.m2e.dashboard.enums.ESBServiceType.*;

@Service
public class AFFSESBService {
    private static final Logger logger = LoggerFactory.getLogger(AFFSESBService.class);
    private final ESBClientService esbClientService;

    @Autowired
    public AFFSESBService(ESBClientService esbClientService, AccountStatusRepository accountStatusRepository) {
        this.esbClientService = esbClientService;
    }

    public ApiResponse<AFFSTrxHistoryResp> getAFFSTrxHistoryMY(ESBAFFSAcctActivityInquiryReqDTO request) {
        logger.trace("getAFFSTrxHistory MY");
        return esbClientService.execute(request, AF_FS_ACCT_ACTIVITY_INQUIRY_MY, AFFSTrxHistoryResp::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFAcctListESBDTO> getAFFSCertDetailsMY(List<ESBAFFSCertDetailsReqDTO> request) {
        logger.trace("getAFFSAcctDetails");
        return esbClientService.execute(request, AF_FS_CERT_DETAILS_MY, AFAcctListESBDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFAcctListESBDTO> getAFFSMasterAcctDetails(List<ESBAcctListingReqDTO> request) {
        logger.trace("getAFFSMasterAcctDetails");
        return esbClientService.execute(request, AF_FS_MASTER_ACCT_DETAILS_MY, AFAcctListESBDTO::new, PropertyServiceEnum.ESBMY);
    }

    public ApiResponse<AFAcctListESBDTO> getAFFSAcctDetailsSG(List<ESBAcctListingReqDTO> request) {
        logger.trace("getAFFSSGAcctDetails");
        return esbClientService.execute(request, AF_FS_ACCT_DETAILS_SG, AFAcctListESBDTO::new, PropertyServiceEnum.ESBSG);
    }
}
