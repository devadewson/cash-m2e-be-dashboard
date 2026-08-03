//package com.m2e.dashboard.service.deposit.ca.jms.account.acctstats.sg;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.m2e.dashboard.config.UserAndCorp;
//import com.m2e.dashboard.config.jms.ESBServiceMapping;
//import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctListingDTO;
//import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctStatsDTO;
//import com.m2e.dashboard.enums.AcctStatusEnum;
//import com.m2e.dashboard.enums.ESBServiceType;
//import com.m2e.dashboard.jms.common.response.ESBDTO;
//import com.m2e.dashboard.jms.deposit.ca.request.account.balanceinquiry.ESBDepositCAAcctListReqDTO;
//import com.m2e.dashboard.jms.deposit.ca.response.account.balanceinquiry.ESBDepositCAAcctBalanceInquiryMsgBodyDTO;
//import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDTO;
//import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDetailDTO;
//import com.m2e.dashboard.projection.ConvertFXRateProjection;
//import com.m2e.dashboard.projection.DepositAcctDetailPageProjection;
//import com.m2e.dashboard.repository.AccountRepository;
//import com.m2e.dashboard.repository.CorpRepository;
//import com.m2e.dashboard.repository.ExchangeRateRepository;
//import com.m2e.dashboard.service.esb.ESBMetadataService;
//import com.m2e.dashboard.service.esb.ESBResponseProcessor;
//import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
//import com.m2e.dashboard.util.ESBValueUtil;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.math.BigDecimal;
//import java.math.RoundingMode;
//import java.util.List;
//import java.util.Optional;
//
//@Slf4j
//@Service
//@ESBServiceMapping(ESBServiceType.DEPOSIT_CA_ACCT_STATS_SG)
//public class DepositCAAcctStatsInquirySGResponseProcessor implements ESBResponseProcessor<DepositAcctStatsDTO> {
//
//    private final ObjectMapper objectMapper;
//    private final AccountRepository accountRepository;
//    private final ESBMetadataService metadataService;
//    private final CorpRepository corpRepository;
//    private final ExchangeRateRepository exchangeRateRepository;
//
//    @Autowired
//    public DepositCAAcctStatsInquirySGResponseProcessor(ObjectMapper objectMapper, AccountRepository accountRepository, ESBMetadataService metadataService, CorpRepository corpRepository, ExchangeRateRepository exchangeRateRepository) {
//        this.objectMapper = objectMapper;
//        this.accountRepository = accountRepository;
//        this.metadataService = metadataService;
//        this.corpRepository = corpRepository;
//        this.exchangeRateRepository = exchangeRateRepository;
//    }
//
//    @Override
//    public DepositAcctStatsDTO processResponse(ESBDTO result, Object request) {
//        ESBDepositCAAcctListReqDTO receivedRequest = objectMapper.convertValue(request, ESBDepositCAAcctListReqDTO.class);
//        ESBDepositCAAcctBalanceInquiryMsgBodyDTO respMsgBody = objectMapper.convertValue(result.getMessage().getMsgBody(), ESBDepositCAAcctBalanceInquiryMsgBodyDTO.class);
//        DepositAcctStatsDTO depositAcctStatsDTO = new DepositAcctStatsDTO();
//        DepositAcctListingDTO depositAcctListingDTO = new DepositAcctListingDTO();
//        depositAcctListingDTO.setDatas(receivedRequest.getDatas());
//        depositAcctListingDTO.setCorpCifNo(receivedRequest.getCorpCifNo());
//        if (respMsgBody != null && respMsgBody.getAccounts() != null) {
//            List<ESBDepositCAAccountDTO> accounts = respMsgBody.getAccounts();
//            // Set balance values from ESB Host for real-time data
//            accounts.forEach(account -> {
//                String accountNumber = ESBValueUtil.formatSgCAEsbAcctNumber(account.getAccountNumber());
//                String currency = account.getCurrency();
//                Optional<String> ledgerBalance;
//                Optional<String> availableBalance = extractValueFromLabel(account, "availableBalance");
//                Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetails(accountNumber, currency);
//                if (accountData.isPresent() && accountData.get().getAccountType().equals("CA")) {
//                    ledgerBalance = extractValueFromLabel(account, "ledgerBalance");
//                } else {
//                    ledgerBalance = extractValueFromLabel(account, "currentBalance");
//                }
//                depositAcctListingDTO.getDatas().getData().stream()
//                        .filter(dto -> ESBValueUtil.isMatchingAccountESB(accountNumber, currency, dto))
//                        .forEach(dto -> {
//                            boolean isClosed = AcctStatusEnum.CLOSED.name().equalsIgnoreCase(dto.getAcctStatusDesc()); //check whether acct status is CLOSED
//                            dto.setLedgerBalance(isClosed ? null : ledgerBalance.orElse(null));
//                            dto.setAvailableBalance(isClosed ? null : availableBalance.orElse(null));
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
//        //calculate and set total balances
//        depositAcctStatsDTO.setTotalLedgerBal(getTotalLedgerBalance(depositAcctListingDTO, receivedRequest.getUserAndCorp()));
//        depositAcctStatsDTO.setTotalAvailableBal(getTotalAvailableBalance(depositAcctListingDTO, receivedRequest.getUserAndCorp()));
//        // esb metadata injection
//        metadataService.attachMetadata(result, depositAcctStatsDTO);
//        return depositAcctStatsDTO;
//    }
//
//    public Optional<String> extractValueFromLabel(ESBDepositCAAccountDTO account, String label) {
//        return account.getDetails().stream()
//                .filter(detail -> label.equals(detail.getName()))
//                .map(ESBDepositCAAccountDetailDTO::getValue)
//                .map(ESBValueUtil::formatSgEsbAmountValue) //do formatting for 15 digits with 2 decimal places from SG ESB response
//                .findFirst();
//    }
//
//    public String getTotalLedgerBalance(DepositAcctListingDTO depositAcctListingDTO, UserAndCorp userAndCorp) {
//        if (depositAcctListingDTO == null || depositAcctListingDTO.getDatas() == null) {
//            return "0"; // Return zero if DTO or data is null
//        }
//        List<DepositAcctSummarySpDTO> accountList = depositAcctListingDTO.getDatas().getData();
//        BigDecimal totalBalance = accountList.stream()
//                //convert ledger balance String to BigDecimal before converting currency
//                .map(account -> {
//                    String ledgerBalStr = account.getLedgerBalance();
//                    if (ledgerBalStr == null || ledgerBalStr.trim().isEmpty()) {
//                        return BigDecimal.ZERO;
//                    }
//                    try {
//                        BigDecimal amount = new BigDecimal(ledgerBalStr.replaceAll("[^\\d.+\\-]", ""));
//                        return convertAmountToBaseCurrency(userAndCorp.getCorpId(), account.getCurrency(), amount);
//                    } catch (NumberFormatException e) {
//                        return BigDecimal.ZERO;
//                    }
//                })
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        return totalBalance.toPlainString();
//    }
//
//    public String getTotalAvailableBalance(DepositAcctListingDTO depositAcctListingDTO, UserAndCorp userAndCorp) {
//        if (depositAcctListingDTO == null || depositAcctListingDTO.getDatas() == null) {
//            return "0"; // Return zero if DTO or data is null
//        }
//        List<DepositAcctSummarySpDTO> accountList = depositAcctListingDTO.getDatas().getData();
//        BigDecimal totalBalance = accountList.stream()
//                // Safely convert ledger balance string to BigDecimal before currency conversion
//                .map(account -> {
//                    String availableBalStr = account.getAvailableBalance();
//                    if (availableBalStr == null || availableBalStr.trim().isEmpty()) {
//                        return BigDecimal.ZERO;
//                    }
//                    try {
//                        BigDecimal amount = new BigDecimal(availableBalStr.replaceAll("[^\\d.+\\-]", ""));
//                        return convertAmountToBaseCurrency(userAndCorp.getCorpId(), account.getCurrency(), amount);
//                    } catch (NumberFormatException e) {
//                        return BigDecimal.ZERO;
//                    }
//                })
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//        return totalBalance.toPlainString();
//    }
//
//    public BigDecimal convertAmountToBaseCurrency(String corpId, String fromCurrency, BigDecimal amount) {
//        try {
//            Optional<String> providerOpt = corpRepository.findProviderByCorpId(corpId);
//            if (providerOpt.isEmpty()) {
//                log.error("Deposit CA MY Acct stats missing provider for corpId: {}", corpId);
//                return BigDecimal.ZERO;
//            }
//            String provider = providerOpt.get();
//
//            Optional<String> baseCurrencyOpt = exchangeRateRepository.findBaseCurrencyByCorpId(corpId);
//            String baseCurrency = baseCurrencyOpt.orElse(null);
//            log.info("Deposit CA SG Acct Stats Starting currency conversion — corpId: {}, fromCurrency: {}, baseCurrency: {}, amount: {}", corpId, fromCurrency, baseCurrency, amount);
//
//            Optional<ConvertFXRateProjection> fxOpt = exchangeRateRepository.findByProviderAndFromCurrency(provider, fromCurrency);
//            if (fxOpt.isEmpty()) {
//                log.info("Deposit CA SG Acct stats no exchange rate found for provider {} and currency {}", provider, fromCurrency);
//                return amount;  //no need to convert for provider of baseCurrency
//            }
//
//            ConvertFXRateProjection fx = fxOpt.get();
//            BigDecimal buyRate = BigDecimal.valueOf(fx.getBuyRate());
//            BigDecimal conversionUnit = new BigDecimal(fx.getConversionUnit());
//
//            BigDecimal convertedAmount;
//
//            if (fromCurrency.equalsIgnoreCase(baseCurrency)) {
//                convertedAmount = amount; //no conversion needed
//            } else if (buyRate.compareTo(BigDecimal.ZERO) == 0) {
//                convertedAmount = BigDecimal.ZERO; //avoid division by zero
//            } else {
//                convertedAmount = amount.multiply(buyRate).divide(conversionUnit, 2, RoundingMode.HALF_UP);
//            }
//            log.info("Deposit CA SG Account Stats finish currency conversion — corpId: {}, fromCurrency: {},fromCurrency: {}, convertedAmount: {}", corpId, fromCurrency, baseCurrency, convertedAmount);
//            return convertedAmount;
//        } catch (Exception e) {
//            log.error("Error when doing conversion for SG Deposit CA Acct Stats: " + e.getMessage());
//            return BigDecimal.ZERO;
//        }
//    }
//}
