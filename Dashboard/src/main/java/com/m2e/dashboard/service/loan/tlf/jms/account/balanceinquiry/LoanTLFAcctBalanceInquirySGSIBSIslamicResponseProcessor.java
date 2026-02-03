package com.m2e.dashboard.service.loan.tlf.jms.account.balanceinquiry;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.m2e.dashboard.config.jms.ESBServiceMapping;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.loan.tlf.response.LNTLFAcctListingDTO;
import com.m2e.dashboard.dto.loan.tlf.response.LNTLFDTO;
import com.m2e.dashboard.jms.common.response.ESBDTO;
import com.m2e.dashboard.jms.loan.tlf.response.account.balanceinquiry.ESBLoanTLFAcctBalanceInquirySGMsgBodyDTO;
import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFAccountSGDTO;
import com.m2e.dashboard.mapper.loan.tlf.ESBLoanTLFSGAcctBalanceInquiryMapper;
import com.m2e.dashboard.service.AsyncMappingService;
import com.m2e.dashboard.service.esb.ESBMetadataService;
import com.m2e.dashboard.service.esb.ESBResponseProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.m2e.dashboard.enums.ESBServiceType.LOAN_TLF_ACCT_BALANCE_INQUIRY_SG_SIBS_ISLAMIC;

@Service
@ESBServiceMapping(LOAN_TLF_ACCT_BALANCE_INQUIRY_SG_SIBS_ISLAMIC)
public class LoanTLFAcctBalanceInquirySGSIBSIslamicResponseProcessor implements ESBResponseProcessor<LNTLFAcctListingDTO> {

    private final ObjectMapper objectMapper;
    private final ESBLoanTLFSGAcctBalanceInquiryMapper esbLoanTLFSGAcctBalanceInquiryMapper;
    private final AsyncMappingService asyncMappingService;
    private final ESBMetadataService metadataService;

    @Autowired
    public LoanTLFAcctBalanceInquirySGSIBSIslamicResponseProcessor(ObjectMapper objectMapper, ESBLoanTLFSGAcctBalanceInquiryMapper esbLoanTLFSGAcctBalanceInquiryMapper, AsyncMappingService asyncMappingService, ESBMetadataService metadataService) {
        this.objectMapper = objectMapper;
        this.esbLoanTLFSGAcctBalanceInquiryMapper = esbLoanTLFSGAcctBalanceInquiryMapper;
        this.asyncMappingService = asyncMappingService;
        this.metadataService = metadataService;
    }

    @Override
    public LNTLFAcctListingDTO processResponse(ESBDTO result, Object request) {
        ESBLoanTLFAcctBalanceInquirySGMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBLoanTLFAcctBalanceInquirySGMsgBodyDTO.class);
        LNTLFAcctListingDTO lntlfAcctListing = new LNTLFAcctListingDTO();
        metadataService.attachMetadata(result, lntlfAcctListing);

        if (respMsgBody != null) {
            List<ESBLoanTLFAccountSGDTO> data = respMsgBody.getAccounts();
            if (data != null && !data.isEmpty()) {
                CompletableFuture<List<LNTLFDTO>> futureResult = asyncMappingService.mapToListAsync(data, esbLoanTLFSGAcctBalanceInquiryMapper::mapToLoanTLFSummaryResp);

                SearchCollectionResponse<LNTLFDTO> acctList = new SearchCollectionResponse<>();
                acctList.setData(futureResult.join());
                lntlfAcctListing.setDatas(acctList);
            }
        }
        return lntlfAcctListing;
    }
}
