//package com.m2e.dashboard.service.deposit.fd.jms.account.acctstats.sg;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.jms.ESBServiceMapping;
//import com.m2e.dashboard.dto.deposit.fd.response.DepositFDAcctStatsDTO;
//import com.m2e.dashboard.dto.deposit.fd.response.DepositFDAcctSummaryDTO;
//import com.m2e.dashboard.enums.AcctStatusEnum;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
//import com.m2e.dashboard.jms.deposit.fd.request.account.balanceinquiry.ESBDepositFDAcctListReqDTO;
//import com.m2e.dashboard.jms.deposit.fd.response.account.balanceinquiry.ESBDepositFDAccountDTO;
//import com.m2e.dashboard.jms.deposit.fd.response.account.balanceinquiry.ESBDepositFDAccountDetailDTO;
//import com.m2e.dashboard.jms.deposit.fd.response.account.balanceinquiry.ESBDepositFDAcctBalanceInquiryMsgBodyDTO;
//import com.m2e.dashboard.service.esb.ESBMetadataService;
//import com.m2e.dashboard.service.esb.ESBResponseProcessor;
//import com.m2e.dashboard.sp.deposit.fd.response.DepositFDAcctSummarySpDTO;
//import com.m2e.dashboard.util.ESBValueUtil;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Optional;
//
//@Service
//@ESBServiceMapping(ESBServiceType.DEPOSIT_FD_ACCT_STATS_SG)
//public class DepositFDAcctStatsInquirySGResponseProcessor implements ESBResponseProcessor<DepositFDAcctStatsDTO> {
//    private final ObjectMapper objectMapper;
//    private ESBMetadataService metadataService;
//
//    @Autowired
//    public DepositFDAcctStatsInquirySGResponseProcessor(ObjectMapper objectMapper, ESBMetadataService metadataService) {
//        this.objectMapper = objectMapper;
//        this.metadataService = metadataService;
//    }
//
//    @Override
//    public DepositFDAcctStatsDTO processResponse(ESBDTO result, Object request) {
//        ESBDepositFDAcctListReqDTO receivedRequest = objectMapper.convertValue(request, ESBDepositFDAcctListReqDTO.class);
//        ESBDepositFDAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBDepositFDAcctBalanceInquiryMsgBodyDTO.class);
//        DepositFDAcctSummaryDTO depositFDAcctSummaryDTO = new DepositFDAcctSummaryDTO();
//        DepositFDAcctStatsDTO depositFDAcctStatsDTO = new DepositFDAcctStatsDTO();
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
//                        .filter(dto -> isAcctMatched(ESBValueUtil.formatSgEsbAcctNumber(accountNumber), dto.getAcctNumber()))
//                        .forEach(dto -> {
//                            dto.setNoOfCertificates(ESBValueUtil.removeLeadingZeroes(certCount.orElse(null)));
//                            boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(dto.getAcctStatusDesc()); //check whether acct status is CLOSED
//                            dto.setTotalPrincipalAmt(isClosed ? null : totalPrincipalAmt.orElse(null));
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
//        //calculate and set total balances
//        depositFDAcctStatsDTO.setConsolidatedPrincipalAmt(getTotalConsolidatedPrincipalAmt(depositFDAcctSummaryDTO));
//        depositFDAcctStatsDTO.setTotalOfCertificates(getTotalCertsCount(depositFDAcctSummaryDTO));
//        // esb metadata injection
//        metadataService.attachMetadata(result, depositFDAcctStatsDTO);
//        return depositFDAcctStatsDTO;
//    }
//
//    public Optional<String> extractValueFromLabel(ESBDepositFDAccountDTO account, String label) {
//        return account.getDetails().stream()
//                .filter(detail -> label.equals(detail.getName()))
//                .map(ESBDepositFDAccountDetailDTO::getValue)
//                .map(value -> label.equals("certsCount") ? value : ESBValueUtil.formatSgEsbAmountValue(value)) //do formatting for amount only, not certsCount
//                .findFirst();
//    }
//
//    private boolean isAcctMatched(String esbAcctNumber, String acctNumber) {
//        if (esbAcctNumber == null || acctNumber == null) {
//            return false;
//        }
//        return esbAcctNumber.equals(acctNumber);
//    }
//
//    public String getTotalCertsCount(DepositFDAcctSummaryDTO depositAcctListingDTO) {
//        if (depositAcctListingDTO == null || depositAcctListingDTO.getDatas() == null) {
//            return null; // Return null if DTO or data is null
//        }
//        List<DepositFDAcctSummarySpDTO> accountList = depositAcctListingDTO.getDatas().getData();
//        int totalCertCount = accountList.stream()
//                .map(account -> account.getNoOfCertificates() != null && !account.getNoOfCertificates().isEmpty()
//                        ? Integer.parseInt(account.getNoOfCertificates())
//                        : 0)
//                .reduce(0, Integer::sum);
//        return String.valueOf(totalCertCount);
//    }
//
//    public String getTotalConsolidatedPrincipalAmt(DepositFDAcctSummaryDTO depositAcctListingDTO) {
//        if (depositAcctListingDTO == null || depositAcctListingDTO.getDatas() == null) {
//            return null; // Return zero if DTO or data is null
//        }
//        List<DepositFDAcctSummarySpDTO> accountList = depositAcctListingDTO.getDatas().getData();
//        BigDecimal totalBalance = accountList.stream()
//                .map(account -> account.getTotalPrincipalAmt() != null
//                        ? new BigDecimal(account.getTotalPrincipalAmt().replaceAll(",", ""))
//                        : BigDecimal.ZERO)
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        return totalBalance.toPlainString();
//    }
//}
