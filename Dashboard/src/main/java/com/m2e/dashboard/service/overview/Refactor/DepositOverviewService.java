package com.m2e.dashboard.service.overview.Refactor;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctListingDTO;
import com.m2e.dashboard.dto.overview.response.AssetLiabilitiesResDTO;
import com.m2e.dashboard.dto.overview.response.OverviewResDTO;
import com.m2e.dashboard.dto.overview.response.OverviewSubResDTO;
import com.m2e.dashboard.enums.OvSubProductEnum;
import com.m2e.dashboard.enums.OverviewEnum;
import com.m2e.dashboard.model.Trapi.Response.KursAllResponse;
import com.m2e.dashboard.service.deposit.ca.Rest.DepositAllAccountListService;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service untuk deposit overview dengan currency conversion.
 *
 * Flow:
 * 1. Fetch kurs dari Redis (via KursService)
 * 2. Fetch akun dari BTS (via DepositAllAccountListService)
 * 3. Map akun ke DTO (via DepositAccountMapper)
 * 4. Konversi amount ke target currency (via CurrencyConverterService)
 * 5. Agregasi & return response
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepositOverviewService {

    private final DepositAllAccountListService depositAllAccountListService;
    private final KursService kursService;
    private final CurrencyConverterService currencyConverter;
    private final DepositAccountMapper accountMapper;

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss '(GMT+8)'");

    /**
     * Get all deposit accounts with converted amounts.
     */
    public OverviewResDTO getDepositOverview(UserAndCorp userAndCorp, String baseCurrency, String corpCifNo) {
        log.info("=== DepositOverviewService.getDepositOverview START ===");
        log.info("User: {}, Corp: {}, CIF: {}, Target Currency: {}",
                userAndCorp.getUserId(), userAndCorp.getCorpId(), corpCifNo, baseCurrency);

        OverviewResDTO overviewResDto = new OverviewResDTO();
        overviewResDto.setId(OverviewEnum.DEPOSIT.name());
        overviewResDto.setTitle(OverviewEnum.DEPOSIT.getDescription());
        overviewResDto.setProductAccType(OverviewEnum.DEPOSIT.getTab());

        String targetCurrency = (baseCurrency != null) ? baseCurrency : "IDR";

        // Fetch & convert accounts
        List<OverviewSubResDTO> convertedAccounts = fetchAndConvertAccounts(userAndCorp, corpCifNo, targetCurrency);

        convertedAccounts.sort(null);
        overviewResDto.setAccount(convertedAccounts);
        overviewResDto.setLastUpdatedDate(OffsetDateTime.now().format(DATE_FORMAT));

        logDepositSummary(convertedAccounts, targetCurrency);
        return overviewResDto;
    }

    /**
     * Get deposit asset & liabilities summary with converted amounts.
     */
    public ApiResponse<AssetLiabilitiesResDTO> getDepositAssetLiabilities(
            UserAndCorp userAndCorp, String baseCurrency, String corpCifNo) {

        log.info("=== DepositOverviewService.getDepositAssetLiabilities START ===");

        ApiResponse<AssetLiabilitiesResDTO> apiResponse = new ApiResponse<>();
        AssetLiabilitiesResDTO result = new AssetLiabilitiesResDTO();

        String targetCurrency = (baseCurrency != null) ? baseCurrency : "IDR";
        result.setCurrency(targetCurrency);

        // 1. Fetch & convert all accounts
        List<OverviewSubResDTO> convertedAccounts = fetchAndConvertAccounts(userAndCorp, corpCifNo, targetCurrency);

        // 2. Aggregate by sub-product
        aggregateBySubProduct(result, convertedAccounts);

        // 3. Deposits are pure assets, zero out all liabilities
        zeroOutAllLiabilities(result);

        // 4. Calculate total asset & ratios
        BigDecimal totalAsset = result.getTotalCA()
                .add(result.getTotalMFCA())
                .add(result.getTotalFCA())
                .add(result.getTotalFD())
                .add(result.getTotalTD())
                .add(result.getTotalVA())
                .add(result.getTotalDpOther());

        result.setTotalAsset(totalAsset);
        result.setTotalLiabilities(BigDecimal.ZERO);
        result.setTotalAssetRatio(totalAsset);
        result.setTotalLiabilitiesRatio(BigDecimal.ZERO);
        result.setWorkingRatio(BigDecimal.ZERO);
        result.setQuickRatio(BigDecimal.ZERO);

        // 5. Last updated date
        OffsetDateTime lastUpdate = OffsetDateTime.now().minusDays(1);
        result.setLastUpdatedDate(lastUpdate.format(DATETIME_FORMAT));

        apiResponse.setMsgBody(result);
        apiResponse.setMsgHeader(new ErrorResponse("0"));

        log.info("=== DepositOverviewService.getDepositAssetLiabilities END ===");
        log.info("Total Asset ({}): {}", targetCurrency, totalAsset);

        return apiResponse;
    }

    /**
     * Get detail overview for a specific deposit sub-type with converted amounts.
     */
    public OverviewResDTO getDepositSubTypeOverview(
            UserAndCorp userAndCorp, OvSubProductEnum subProduct, String baseCurrency, String corpCifNo) {

        log.info("=== getDepositSubTypeOverview: {} ===", subProduct);

        OverviewResDTO overviewResDto = new OverviewResDTO();
        overviewResDto.setId(subProduct.name());
        overviewResDto.setTitle(getSubProductTitle(subProduct));
        overviewResDto.setProductAccType("assets");

        String targetCurrency = (baseCurrency != null) ? baseCurrency : "IDR";

        // Fetch & convert accounts
        List<OverviewSubResDTO> convertedAccounts = fetchAndConvertAccounts(userAndCorp, corpCifNo, targetCurrency);

        // Filter by subProduct
        List<OverviewSubResDTO> filteredAccounts = convertedAccounts.stream()
                .filter(a -> a.getSubProduct() == subProduct)
                .collect(Collectors.toList());

        filteredAccounts.sort(null);
        overviewResDto.setAccount(filteredAccounts);
        overviewResDto.setLastUpdatedDate(OffsetDateTime.now().format(DATE_FORMAT));

        log.info("SubType {} - {} accounts found (in {})", subProduct, filteredAccounts.size(), targetCurrency);
        return overviewResDto;
    }


    /**
     * Fetch accounts from BTS, map, and convert to target currency.
     */
    private List<OverviewSubResDTO> fetchAndConvertAccounts(
            UserAndCorp userAndCorp, String corpCifNo, String targetCurrency) {

        try {
            // Step 1: Fetch kurs from Redis
            log.info("Step 1: Fetching kurs from Redis...");
            KursAllResponse kursResponse = kursService.getKursAll();

            if (kursResponse == null || kursResponse.getRespData() == null) {
                log.warn("Kurs data not available, returning accounts without conversion");
                return fetchRawAccounts(userAndCorp, corpCifNo);
            }

            // Step 2: Build rate map
            log.info("Step 2: Building rate map...");
            Map<String, BigDecimal> rateMap = currencyConverter.buildRateMap(kursResponse.getRespData());

            // Step 3: Fetch raw accounts from BTS
            log.info("Step 3: Fetching accounts from BTS...");
            List<DepositAcctSummarySpDTO> btsAccounts = fetchRawBtsAccounts(userAndCorp, corpCifNo);

            if (btsAccounts.isEmpty()) {
                log.warn("No accounts fetched from BTS");
                return List.of();
            }

            // Step 4: Map to DTO
            log.info("Step 4: Mapping {} accounts to DTO...", btsAccounts.size());
            List<OverviewSubResDTO> mappedAccounts = accountMapper.mapAll(btsAccounts);

            // Step 5: Convert all amounts
            log.info("Step 5: Converting amounts to {}...", targetCurrency);
            List<OverviewSubResDTO> convertedAccounts = convertAllAccounts(mappedAccounts, targetCurrency, rateMap);

            log.info("Step 6: Done! {} accounts converted to {}", convertedAccounts.size(), targetCurrency);
            return convertedAccounts;

        } catch (Exception e) {
            log.error("Error in fetchAndConvertAccounts: {}", e.getMessage(), e);
            // Fallback: return raw accounts without conversion
            return fetchRawAccounts(userAndCorp, corpCifNo);
        }
    }

    /**
     * Fetch raw BTS accounts without mapping.
     */
    private List<DepositAcctSummarySpDTO> fetchRawBtsAccounts(UserAndCorp userAndCorp, String corpCifNo) {
        try {
            AccountListReq req = createAccountListRequest(corpCifNo);
            ApiResponse<DepositAcctListingDTO> response = depositAllAccountListService.getAllAccounts(userAndCorp, req);

            if (response.getMsgBody() != null && response.getMsgBody().getDatas() != null) {
                return response.getMsgBody().getDatas().getData();
            }
        } catch (Exception e) {
            log.error("Error fetching raw BTS accounts: {}", e.getMessage(), e);
        }
        return List.of();
    }

    /**
     * Fallback: fetch raw accounts without conversion (existing behavior).
     */
    private List<OverviewSubResDTO> fetchRawAccounts(UserAndCorp userAndCorp, String corpCifNo) {
        List<OverviewSubResDTO> result = new ArrayList<>();
        try {
            AccountListReq req = createAccountListRequest(corpCifNo);
            ApiResponse<DepositAcctListingDTO> response = depositAllAccountListService.getAllAccounts(userAndCorp, req);

            if (response.getMsgBody() != null && response.getMsgBody().getDatas() != null) {
                List<DepositAcctSummarySpDTO> btsAccounts = response.getMsgBody().getDatas().getData();
                log.info("Fetched {} accounts from BTS (without conversion)", btsAccounts.size());
                return accountMapper.mapAll(btsAccounts);
            }
        } catch (Exception e) {
            log.error("Error fetching all accounts from BTS: {}", e.getMessage());
        }
        return result;
    }

    /**
     * Convert all accounts to target currency.
     */
    private List<OverviewSubResDTO> convertAllAccounts(
            List<OverviewSubResDTO> accounts,
            String targetCurrency,
            Map<String, BigDecimal> rateMap) {

        return accounts.stream()
                .map(account -> convertSingleAccount(account, targetCurrency, rateMap))
                .collect(Collectors.toList());
    }

    /**
     * Convert single account to target currency.
     */
    private OverviewSubResDTO convertSingleAccount(
            OverviewSubResDTO account,
            String targetCurrency,
            Map<String, BigDecimal> rateMap) {

        String originalCurrency = account.getCurrency();
        BigDecimal originalAmount = account.getTotalAmount() != null ?
                account.getTotalAmount() : BigDecimal.ZERO;

        if (originalAmount.compareTo(BigDecimal.ZERO) <= 0) {
            // Zero amount, just update currency label
            account.setCurrency(targetCurrency);
            return account;
        }

        // Convert amount
        BigDecimal convertedAmount = currencyConverter.convert(
                originalAmount,
                originalCurrency,
                targetCurrency,
                rateMap
        );

        // Update account
        account.setTotalAmount(convertedAmount);
        account.setCurrency(targetCurrency);

        log.debug("Converted: {} {} → {} {}",
                originalAmount, originalCurrency,
                convertedAmount, targetCurrency);

        return account;
    }

    /**
     * Aggregate converted accounts by sub-product.
     */
    private void aggregateBySubProduct(AssetLiabilitiesResDTO result, List<OverviewSubResDTO> accounts) {
        BigDecimal totalCA = BigDecimal.ZERO;
        BigDecimal totalSA = BigDecimal.ZERO;
        BigDecimal totalMFCA = BigDecimal.ZERO;
        BigDecimal totalFCA = BigDecimal.ZERO;
        BigDecimal totalFD = BigDecimal.ZERO;
        BigDecimal totalTD = BigDecimal.ZERO;
        BigDecimal totalVA = BigDecimal.ZERO;
        BigDecimal totalOther = BigDecimal.ZERO;

        for (OverviewSubResDTO acc : accounts) {
            BigDecimal amount = acc.getTotalAmount() != null ? acc.getTotalAmount() : BigDecimal.ZERO;

            switch (acc.getSubProduct()) {
                case CA:   totalCA = totalCA.add(amount); break;
                case SA:   totalSA = totalSA.add(amount); break;
                case MFCA: totalMFCA = totalMFCA.add(amount); break;
                case FCA:  totalFCA = totalFCA.add(amount); break;
                case FD:   totalFD = totalFD.add(amount); break;
                case TD:   totalTD = totalTD.add(amount); break;
                case VA:   totalVA = totalVA.add(amount); break;
                default:   totalOther = totalOther.add(amount); break;
            }
        }

        result.setTotalCA(totalCA);
        result.setTotalMFCA(totalMFCA);
        result.setTotalFCA(totalFCA);
        result.setTotalFD(totalFD);
        result.setTotalTD(totalTD);
        result.setTotalVA(totalVA);
        result.setTotalDpOther(totalSA.add(totalOther));
        result.setTotalInv(BigDecimal.ZERO);
        result.setTotalLoanOther(BigDecimal.ZERO);
    }

    private AccountListReq createAccountListRequest(String corpCifNo) {
        AccountListReq req = new AccountListReq();
        req.setCorpCifNo(corpCifNo);
        req.setRealTimeIndicator(Boolean.TRUE);
        req.setPageSize(Integer.MAX_VALUE);
        return req;
    }

    private void zeroOutAllLiabilities(AssetLiabilitiesResDTO dto) {
        dto.setTotalOD(BigDecimal.ZERO);
        dto.setTotalTL(BigDecimal.ZERO);
        dto.setTotalRc(BigDecimal.ZERO);
        dto.setTotalCrc(BigDecimal.ZERO);
        dto.setTotalChc(BigDecimal.ZERO);
        dto.setTotalDc(BigDecimal.ZERO);
        dto.setTotalTRD(BigDecimal.ZERO);
        dto.setTotalTRDI(BigDecimal.ZERO);
        dto.setTotalTRDE(BigDecimal.ZERO);
        dto.setTotalTRDG(BigDecimal.ZERO);
        dto.setTotalTRDL(BigDecimal.ZERO);
        dto.setTotalHp(BigDecimal.ZERO);
        dto.setTotalBhp(BigDecimal.ZERO);
        dto.setTotalFs(BigDecimal.ZERO);
        dto.setTotalBD(BigDecimal.ZERO);
        dto.setTotalPbr(BigDecimal.ZERO);
        dto.setTotalLoanOther(BigDecimal.ZERO);
    }

    private String getSubProductTitle(OvSubProductEnum subProduct) {
        switch (subProduct) {
            case CA: return "Current Accounts";
            case SA: return "Savings Accounts";
            case MFCA: return "Mudharabah Current Accounts";
            case FCA: return "Foreign Currency Accounts";
            case FD: return "Fixed Deposits";
            case TD: return "Term Deposits";
            case VA: return "Virtual Accounts";
            case DEPOSIT: return "All Deposits";
            default: return "Deposit Accounts";
        }
    }

    private void logDepositSummary(List<OverviewSubResDTO> accounts, String targetCurrency) {
        log.info("=== Deposit Overview Summary (in {}) ===", targetCurrency);
        log.info("Total accounts: {}", accounts.size());
        accounts.stream()
                .collect(Collectors.groupingBy(
                        OverviewSubResDTO::getSubProduct,
                        Collectors.summarizingDouble(a -> a.getTotalAmount() != null ?
                                a.getTotalAmount().doubleValue() : 0)
                ))
                .forEach((subProduct, summary) ->
                        log.info("  {} : {} accounts | Total: {} {}",
                                subProduct, summary.getCount(), summary.getSum(), targetCurrency));
        log.info("========================================");
    }
}