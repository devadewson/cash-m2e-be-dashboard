//package com.m2e.dashboard.service.loan.tlf.jms.account.balanceinquiry;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.jms.ESBServiceMapping;
//import com.m2e.dashboard.dto.common.SearchCollectionResponse;
//import com.m2e.dashboard.dto.loan.tlf.response.LNTLFAcctListingDTO;
//import com.m2e.dashboard.dto.loan.tlf.response.LNTLFDTO;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
//import com.m2e.dashboard.jms.loan.tlf.response.account.balanceinquiry.ESBLoanTLFAcctBalanceInquiryMsgBodyDTO;
//import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFAccountDTO;
//import com.m2e.dashboard.mapper.loan.tlf.ESBLoanTLFAcctBalanceInquiryMapper;
//import com.m2e.dashboard.service.AsyncMappingService;
//import com.m2e.dashboard.service.esb.ESBMetadataService;
//import com.m2e.dashboard.service.esb.ESBResponseProcessor;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.concurrent.CompletableFuture;
//
//@Service
//@ESBServiceMapping(ESBServiceType.LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_ISLAMIC)
//public class LoanTLFAcctBalanceInquiryMYIslamicResponseProcessor implements ESBResponseProcessor<LNTLFAcctListingDTO> {
//
//    private final ObjectMapper objectMapper;
//    private final ESBLoanTLFAcctBalanceInquiryMapper esbLoanTLFAcctBalanceInquiryMapper;
//    private final AsyncMappingService asyncMappingService;
//    private final ESBMetadataService metadataService;
//
//    @Autowired
//    public LoanTLFAcctBalanceInquiryMYIslamicResponseProcessor(ObjectMapper objectMapper, ESBLoanTLFAcctBalanceInquiryMapper esbLoanTLFAcctBalanceInquiryMapper, AsyncMappingService asyncMappingService, ESBMetadataService metadataService) {
//        this.objectMapper = objectMapper;
//        this.esbLoanTLFAcctBalanceInquiryMapper = esbLoanTLFAcctBalanceInquiryMapper;
//        this.asyncMappingService = asyncMappingService;
//        this.metadataService = metadataService;
//    }
//
//    @Override
//    public LNTLFAcctListingDTO processResponse(ESBDTO result, Object request) {
//        ESBLoanTLFAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBLoanTLFAcctBalanceInquiryMsgBodyDTO.class);
//        LNTLFAcctListingDTO lntlfAcctListing = new LNTLFAcctListingDTO();
//        metadataService.attachMetadata(result, lntlfAcctListing);
//
//        if (respMsgBody != null) {
//            List<ESBLoanTLFAccountDTO> data = respMsgBody.getAccounts();
//            if (data != null && !data.isEmpty()) {
//                CompletableFuture<List<LNTLFDTO>> futureResult = asyncMappingService.mapToListAsync(
//                        data,
//                        dto -> esbLoanTLFAcctBalanceInquiryMapper.mapToLoanTLFSummaryResp(
//                                dto, ESBServiceType.LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_ISLAMIC));
//
//                SearchCollectionResponse<LNTLFDTO> acctList = new SearchCollectionResponse<>();
//                acctList.setData(futureResult.join());
//                lntlfAcctListing.setDatas(acctList);
//            }
//        }
//        return lntlfAcctListing;
//    }
//}
