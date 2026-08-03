//package com.m2e.dashboard.service.deposit.ca.jms.account.balanceinquiry.my;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.jms.ESBServiceMapping;
//import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctListingDTO;
//import com.m2e.dashboard.enums.AcctStatusEnum;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
//import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctListReqDTO;
//import com.m2e.dashboard.jms.deposit.ca.response.account.balanceinquiry.ESBDepositCAAcctBalanceInquiryMsgBodyDTO;
//import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDTO;
//import com.m2e.dashboard.projection.DepositAcctDetailPageProjection;
//import com.m2e.dashboard.repository.AccountRepository;
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
//
//@Service
//@ESBServiceMapping(ESBServiceType.DEPOSIT_CA_ACCT_BALANCE_INQUIRY_MY)
//public class DepositCAAcctBalanceInquiryMYResponseProcessor implements ESBResponseProcessor<DepositAcctListingDTO> {
//
//    private static final Logger logger = LoggerFactory.getLogger(DepositCAAcctBalanceInquiryMYResponseProcessor.class);
//
//    private final ObjectMapper objectMapper;
//    private final AccountRepository accountRepository;
//    private final ESBMetadataService metadataService;
//
//
//    @Autowired
//    public DepositCAAcctBalanceInquiryMYResponseProcessor(ObjectMapper objectMapper, AccountRepository accountRepository, ESBMetadataService metadataService) {
//        this.objectMapper = objectMapper;
//        this.accountRepository = accountRepository;
//        this.metadataService = metadataService;
//    }
//
//    @Override
//    public DepositAcctListingDTO processResponse(ESBDTO result, Object request) {
//        ESBDepositCAAcctListReqDTO receivedRequest = objectMapper.convertValue(request, ESBDepositCAAcctListReqDTO.class);
//        ESBDepositCAAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBDepositCAAcctBalanceInquiryMsgBodyDTO.class);
//        DepositAcctListingDTO depositAcctListingDTO = new DepositAcctListingDTO();
//        depositAcctListingDTO.setDatas(receivedRequest.getDatas());
//        depositAcctListingDTO.setCorpCifNo(receivedRequest.getCorpCifNo());
//        if (respMsgBody != null && respMsgBody.getAccounts() != null) {
//            List<ESBDepositCAAccountDTO> accounts = respMsgBody.getAccounts();
//            // Set balance values from ESB Host for real-time data
//            accounts.forEach(account -> {
//                String accountNumber = account.getAccountNumber();
//                String currency = account.getCurrency();
//                Optional<String> ledgerBalance;
//                Optional<String> availableBalance;
//                Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetails(accountNumber, currency);
//                if (accountData.isPresent() && accountData.get().getAccountType().equals("CA")) {
//                    ledgerBalance = extractValueFromLabel(account, "ledgerBalance");
//                    availableBalance = extractValueFromLabel(account, "availableBalance");
//                } else {
//                    ledgerBalance = extractValueFromLabel(account, "currentBalance");
//                    availableBalance = extractValueFromLabel(account, "currentBalance");
//                }
//                depositAcctListingDTO.getDatas().getData().stream()
//                        .filter(dto -> ESBValueUtil.isMatchingAccountESB(accountNumber, currency, dto))
//                        .forEach(dto -> {
//                            boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(dto.getAcctStatusDesc()); //check whether acct status is CLOSED
//                            dto.setLedgerBalance(isClosed ? "-" : ledgerBalance.orElse(null));
//                            dto.setAvailableBalance(isClosed ? "-" : availableBalance.orElse(null));
//                        });
//            });
//        } else {
//            //set null if ESB host returns no values
//            depositAcctListingDTO.getDatas().getData()
//                    .forEach(dto -> {
//                        dto.setLedgerBalance(null);
//                        dto.setAvailableBalance(null);
//                    });
//        }
//        // esb metadata injection
//        metadataService.attachMetadata(result, depositAcctListingDTO);
//        return depositAcctListingDTO;
//    }
//
//    public Optional<String> extractValueFromLabel(ESBDepositCAAccountDTO account, String label) {
//        return account.getDetails().stream()
//                .filter(detail -> label.equals(detail.getName()))
//                .map(detail -> ESBValueUtil.removeLeadingPlus(detail.getValue()))
//                .findFirst();
//    }
//}
