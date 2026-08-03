//package com.m2e.dashboard.service.deposit.fd.jms.account.balanceinquiry.my;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.jms.ESBServiceMapping;
//import com.m2e.dashboard.dto.deposit.fd.response.DepositFDAcctSummaryDTO;
//import com.m2e.dashboard.enums.AcctStatusEnum;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
//import com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry.ESBDepositFDAcctListReqDTO;
//import com.m2e.dashboard.jms.deposit.fd.response.account.balanceinquiry.ESBDepositFDAccountDTO;
//import com.m2e.dashboard.jms.deposit.fd.response.account.balanceinquiry.ESBDepositFDAccountDetailDTO;
//import com.m2e.dashboard.jms.deposit.fd.response.account.balanceinquiry.ESBDepositFDAcctBalanceInquiryMsgBodyDTO;
//import com.m2e.dashboard.mapper.deposit.fd.ESBDepositFDAcctBalanceInquiryMapper;
//import com.m2e.dashboard.service.AsyncMappingService;
//import com.m2e.dashboard.service.AsyncPaginationService;
//import com.m2e.dashboard.service.esb.ESBMetadataService;
//import com.m2e.dashboard.service.esb.ESBResponseProcessor;
//import com.m2e.dashboard.util.ESBValueUtil;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@ESBServiceMapping(ESBServiceType.DEPOSIT_FD_ACCT_BALANCE_INQUIRY_MY)
//public class DepositFDAcctBalanceInquiryMYResponseProcessor implements ESBResponseProcessor<DepositFDAcctSummaryDTO> {
//    protected static final Logger logger = LoggerFactory.getLogger(DepositFDAcctBalanceInquiryMYResponseProcessor.class);
//    private final ObjectMapper objectMapper;
//    private final AsyncPaginationService asyncPaginationService;
//    private final AsyncMappingService asyncMappingService;
//    private final ESBDepositFDAcctBalanceInquiryMapper esbDepositFDAcctBalanceInquiryMapper;
//    private ESBMetadataService metadataService;
//
//    @Autowired
//    public DepositFDAcctBalanceInquiryMYResponseProcessor(ObjectMapper objectMapper, AsyncPaginationService asyncPaginationService, AsyncMappingService asyncMappingService, ESBDepositFDAcctBalanceInquiryMapper esbDepositFDAcctBalanceInquiryMapper, ESBMetadataService metadataService) {
//        this.objectMapper = objectMapper;
//        this.esbDepositFDAcctBalanceInquiryMapper = esbDepositFDAcctBalanceInquiryMapper;
//        this.asyncPaginationService = asyncPaginationService;
//        this.asyncMappingService = asyncMappingService;
//        this.metadataService = metadataService;
//    }
//
//    @Override
//    public DepositFDAcctSummaryDTO processResponse(ESBDTO result, Object request) {
//        ESBDepositFDAcctListReqDTO receivedRequest = objectMapper.convertValue(request, ESBDepositFDAcctListReqDTO.class);
//        ESBDepositFDAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBDepositFDAcctBalanceInquiryMsgBodyDTO.class);
//        DepositFDAcctSummaryDTO depositFDAcctSummaryDTO = new DepositFDAcctSummaryDTO();
//        depositFDAcctSummaryDTO.setDatas(receivedRequest.getDatas());
//        depositFDAcctSummaryDTO.setCorpCifNo(receivedRequest.getCorpCifNo());
//        if (respMsgBody != null && respMsgBody.getAccounts() != null) {
//            List<ESBDepositFDAccountDTO> accounts = respMsgBody.getAccounts();
//            // Set values from ESB Host for real-time data
//            accounts.forEach(account -> {
//                String accountNumber = account.getAccountNumber();
//                Optional<String> certCount = extractValueFromLabel(account, "certsCount");
//                Optional<String> totalPrincipalAmt = extractValueFromLabel(account, "principalAmount");
//
//                depositFDAcctSummaryDTO.getDatas().getData().stream()
//                        .filter(dto -> dto.getAcctNumber().equals(accountNumber))
//                        .forEach(dto -> {
//                            dto.setNoOfCertificates(ESBValueUtil.removeLeadingZeroes(certCount.orElse(null)));
//                            boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(dto.getAcctStatusDesc()); //check whether acct status is CLOSED
//                            dto.setTotalPrincipalAmt(isClosed ? "-" : totalPrincipalAmt.orElse(null));
//                        });
//            });
//        } else {
//            //set null if ESB host returns no values
//            depositFDAcctSummaryDTO.getDatas().getData()
//                    .forEach(dto -> {
//                        dto.setNoOfCertificates(null);
//                        dto.setTotalPrincipalAmt(null);
//                    });
//        }
//        // esb metadata injection
//        metadataService.attachMetadata(result, depositFDAcctSummaryDTO);
//        return depositFDAcctSummaryDTO;
//    }
//
//    public Optional<String> extractValueFromLabel(ESBDepositFDAccountDTO account, String label) {
//        return account.getDetails().stream()
//                .filter(detail -> label.equals(detail.getName()))
//                .map(ESBDepositFDAccountDetailDTO::getValue)
//                .findFirst();
//    }
//}
