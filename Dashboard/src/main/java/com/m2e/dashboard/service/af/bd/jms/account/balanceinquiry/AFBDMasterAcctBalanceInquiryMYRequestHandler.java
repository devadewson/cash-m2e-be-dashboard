package com.m2e.dashboard.service.af.bd.jms.account.balanceinquiry;

import com.m2e.dashboard.config.jms.ESBIntegrationProperties;
import com.m2e.dashboard.enums.AcctTypEnum;
import com.m2e.dashboard.enums.PropertyServiceEnum;
import com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry.ESBAFBDAcctBalanceInquiryMsgBodyReqDTO;
import com.m2e.dashboard.jms.af.bd.request.account.balanceinquiry.ESBAFBDFacilityListReqDTO;
import com.m2e.dashboard.jms.af.bd.request.account.details.ESBAFBDAcctDetailsReqDTO;
import com.m2e.dashboard.jms.common.ESBPropertiesDTO;
import com.m2e.dashboard.jms.common.request.ESBMsgReqDTO;
import com.m2e.dashboard.jms.common.request.ESBReqDTO;
import com.m2e.dashboard.repository.AccountProductTypeMatrixRepository;
import com.m2e.dashboard.service.esb.client.ESBClient;
import com.m2e.dashboard.service.esb.ESBRequestHandler;
import com.m2e.model.sso.base.AccountProductTypeMatrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.m2e.dashboard.enums.ESBServiceType.AF_BD_MASTER_ACCT_BALANCE_INQUIRY_MY;

@Service
public class AFBDMasterAcctBalanceInquiryMYRequestHandler implements ESBRequestHandler<ESBAFBDFacilityListReqDTO, ESBReqDTO> {
    private static final Logger log = LoggerFactory.getLogger(AFBDMasterAcctBalanceInquiryMYRequestHandler.class);

    private final ESBIntegrationProperties esbIntegrationProperties;
    private final ESBClient esbClient;
    private final AccountProductTypeMatrixRepository matrixRepository;

    @Autowired
    public AFBDMasterAcctBalanceInquiryMYRequestHandler(ESBIntegrationProperties esbIntegrationProperties, ESBClient esbClient, AccountProductTypeMatrixRepository matrixRepository) {
        this.esbIntegrationProperties = esbIntegrationProperties;
        this.esbClient = esbClient;
        this.matrixRepository = matrixRepository;
    }

    @Override
    public ESBReqDTO prepareRequest(ESBAFBDFacilityListReqDTO request) {
        ESBPropertiesDTO configProperties = esbIntegrationProperties.getIbmMq(PropertyServiceEnum.AF_BD_GETACCTDETAILS_MY.getValue());
        log.info("Properties info for getActivityInquiry ESB: {}", configProperties);

        return ESBReqDTO.builder()
                .message(ESBMsgReqDTO.builder()
                        .messageHeader(esbClient.constructMsgHeader(configProperties))
                        .messageBody(ESBAFBDAcctBalanceInquiryMsgBodyReqDTO.builder()
                                .accountListing(convertToESBAcctListingReqDTO(request)).build())
                        .build())
                .build();
    }

    private List<ESBAFBDAcctDetailsReqDTO> convertToESBAcctListingReqDTO(ESBAFBDFacilityListReqDTO summaryDTO) {
        return summaryDTO.getDatas().stream().map(spDTO ->
                ESBAFBDAcctDetailsReqDTO.builder()
                        .accountNumber(spDTO.getMasterAcctNumber())
                        .accountType(getAcctTypeCode(spDTO.getProvider()))
                        .currency(spDTO.getCurrency())
                        .region(spDTO.getProvider())
                        .build()
        ).toList();
    }

    private String getAcctTypeCode(String provider) {
        Optional<AccountProductTypeMatrix> matrix = matrixRepository.findByAcctTypeAndProv(AcctTypEnum.BD.name(), provider);
        return matrix.map(AccountProductTypeMatrix::getEsbAccountTypeCode).orElse(null);
    }

    @Override
    public String getServiceType() {
        return AF_BD_MASTER_ACCT_BALANCE_INQUIRY_MY.name();
    }
}
