package com.m2e.dashboard.service.deposit.fd.jms.account.details.my;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.deposit.fd.request.DepositFDAcctDetailReqDTO;
import com.m2e.dashboard.dto.deposit.fd.response.DepositFDAccountDetailDTO;
import com.m2e.dashboard.enums.ESBServiceType;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.balanceinquiry.ESBDepositCAAcctBalanceInquiryMsgBodyDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDTO;
import com.m2e.dashboard.mapper.deposit.fd.ESBDepositFDAcctDetailsMapper;
import com.m2e.dashboard.repository.CorpRepository;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@ESBServiceMapping(ESBServiceType.DEPOSIT_FD_ACCT_DETAILS_MY)
public class DepositFDAcctDetailsMYResponseProcessor implements ESBResponseProcessor<DepositFDAccountDetailDTO> {
    private final ObjectMapper objectMapper;
    private final ESBDepositFDAcctDetailsMapper esbDepositFDAcctDetailsMapper;
    private final ESBMetadataService metadataService;

    @Autowired
    public DepositFDAcctDetailsMYResponseProcessor(ObjectMapper objectMapper, ESBDepositFDAcctDetailsMapper esbDepositFDAcctDetailsMapper, CorpRepository corpRepository, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbDepositFDAcctDetailsMapper = esbDepositFDAcctDetailsMapper;
        this.metadataService = metadataService;
    }

    @Override
    public DepositFDAccountDetailDTO processResponse(ESBDTO result, Object request) {
        DepositFDAccountDetailDTO depositFDAccountDetailDTO = new DepositFDAccountDetailDTO();
        DepositFDAcctDetailReqDTO receivedRequest = objectMapper.convertValue(request, DepositFDAcctDetailReqDTO.class);
        ESBDepositCAAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBDepositCAAcctBalanceInquiryMsgBodyDTO.class);
        if (respMsgBody != null) {
            List<ESBDepositCAAccountDTO> data = respMsgBody.getAccounts();
            if (data != null && !data.isEmpty()) {
                ESBDepositCAAccountDTO ESBDepositCAAccountDTO = data.get(0);
                depositFDAccountDetailDTO = esbDepositFDAcctDetailsMapper.mapToDepositFDDetailDTO(ESBDepositCAAccountDTO);
            }
        }
        // esb metadata injection
        metadataService.attachMetadata(result, depositFDAccountDetailDTO);
        return depositFDAccountDetailDTO;
    }
}
