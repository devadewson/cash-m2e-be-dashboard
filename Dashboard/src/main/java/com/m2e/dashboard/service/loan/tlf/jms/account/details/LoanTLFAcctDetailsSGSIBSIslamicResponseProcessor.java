//package com.m2e.dashboard.service.loan.tlf.jms.account.details;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.jms.ESBServiceMapping;
//import com.m2e.dashboard.dto.loan.tlf.response.acctdetail.LNTLFDetailSGSIBSDTO;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
//import com.m2e.dashboard.jms.loan.tlf.response.account.balanceinquiry.ESBLoanTLFAcctBalanceInquiryMsgBodyDTO;
//import com.m2e.dashboard.jms.loan.tlf.response.account.details.ESBLoanTLFAccountDTO;
//import com.m2e.dashboard.mapper.loan.tlf.ESBLoanTLFAcctDetailsMapper;
//import com.m2e.dashboard.service.esb.ESBMetadataService;
//import com.m2e.dashboard.service.esb.ESBResponseProcessor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//@Service
//@Slf4j
//@ESBServiceMapping(ESBServiceType.LOAN_TLF_ACCT_DETAILS_SG_SIBS_ISLAMIC)
//public class LoanTLFAcctDetailsSGSIBSIslamicResponseProcessor implements ESBResponseProcessor<LNTLFDetailSGSIBSDTO> {
//    private final ObjectMapper objectMapper;
//    private final ESBLoanTLFAcctDetailsMapper esbLoanTLFAcctDetailsMapper;
//    private final ESBMetadataService metadataService;
//    @Autowired
//    public LoanTLFAcctDetailsSGSIBSIslamicResponseProcessor(ObjectMapper objectMapper, ESBLoanTLFAcctDetailsMapper esbLoanTLFAcctDetailsMapper, ESBMetadataService metadataService) {
//        this.objectMapper = objectMapper;
//        this.esbLoanTLFAcctDetailsMapper = esbLoanTLFAcctDetailsMapper;
//        this.metadataService = metadataService;
//    }
//
//    @Override
//    public LNTLFDetailSGSIBSDTO processResponse(ESBDTO result, Object request) {
//        LNTLFDetailSGSIBSDTO lntlfDetailDTO = new LNTLFDetailSGSIBSDTO();
//        ESBLoanTLFAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBLoanTLFAcctBalanceInquiryMsgBodyDTO.class);
//        if (respMsgBody != null) {
//            List<ESBLoanTLFAccountDTO> data = respMsgBody.getAccounts();
//            if (data != null && !data.isEmpty()) {
//                ESBLoanTLFAccountDTO esbLoanTLFAccountDTO = data.get(0);
//                log.info("ESB Term Loan SIBS islamic account detail result --> {}", esbLoanTLFAccountDTO);
//                lntlfDetailDTO = esbLoanTLFAcctDetailsMapper.mapToLoanAccountDetailSGSIBSIslamicResp(esbLoanTLFAccountDTO);
//            }
//        }
//        // esb metadata injection
//        metadataService.attachMetadata(result, lntlfDetailDTO);
//        return lntlfDetailDTO;
//    }
//}
