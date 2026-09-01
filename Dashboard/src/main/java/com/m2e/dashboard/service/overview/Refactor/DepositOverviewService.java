package com.m2e.dashboard.service.overview.Refactor;

import com.m2e.dashboard.config.CorpCifResolver;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctListingDTO;
import com.m2e.dashboard.dto.overview.Refactor.KursResolution;
import com.m2e.dashboard.dto.overview.response.AssetLiabilitiesResDTO;
import com.m2e.dashboard.dto.overview.response.OverviewResDTO;
import com.m2e.dashboard.dto.overview.response.OverviewSubResDTO;
import com.m2e.dashboard.enums.OvSubProductEnum;
import com.m2e.dashboard.enums.OverviewEnum;
import com.m2e.dashboard.service.deposit.ca.Rest.DepositAllAccountListService;
import com.m2e.dashboard.service.overview.AssetGraph.GraphCurrencyConverter;
import com.m2e.dashboard.service.overview.JobKursService.KursResolver;
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
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Service untuk deposit overview.
 *
 * Listing (/overview, /overview/{subProduct})
 *
 * Summary (/asset) menjumlahkan seluruh rekening, jadi wajib dikonversi
 * ke satu currency sebelum diagregasi.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepositOverviewService {

    private final DepositAllAccountListService depositAllAccountListService;
    private final KursResolver kursResolver;
    private final GraphCurrencyConverter graphConverter;
    private final DepositAccountMapper accountMapper;
    private final CorpCifResolver corpCifResolver;

    private static final String IDR = "IDR";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss '(GMT+8)'");

    /**
     * Get all deposit accounts.
     */
    public OverviewResDTO getDepositOverview(UserAndCorp userAndCorp, String baseCurrency) {

        String corpCifNo = corpCifResolver.resolveCif(userAndCorp.getCorpId());

        log.info("=== getDepositOverview START | user={} corpId={} cif={} ===",
                userAndCorp.getUserId(), userAndCorp.getCorpId(), corpCifNo);

        OverviewResDTO overviewResDto = new OverviewResDTO();
        overviewResDto.setId(OverviewEnum.DEPOSIT.name());
        overviewResDto.setTitle(OverviewEnum.DEPOSIT.getDescription());
        overviewResDto.setProductAccType(OverviewEnum.DEPOSIT.getTab());

        List<OverviewSubResDTO> accounts =
                new ArrayList<>(fetchAccountsRaw(userAndCorp, corpCifNo));

        accounts.sort(null);
        overviewResDto.setAccount(accounts);
        overviewResDto.setLastUpdatedDate(OffsetDateTime.now().format(DATE_FORMAT));

        log.info("=== getDepositOverview END | cif={} accounts={} ===", corpCifNo, accounts.size());
        return overviewResDto;
    }

    /**
     * Get deposit asset & liabilities summary. Seluruh rekening dikonversi
     * ke targetCurrency sebelum dijumlah.
     */
    public ApiResponse<AssetLiabilitiesResDTO> getDepositAssetLiabilities(
            UserAndCorp userAndCorp, String baseCurrency) {

        String corpCifNo = corpCifResolver.resolveCif(userAndCorp.getCorpId());
        String targetCurrency = resolveTargetCurrency(baseCurrency);

        log.info("=== getDepositAssetLiabilities START | cif={} currency={} ===",
                corpCifNo, targetCurrency);

        ApiResponse<AssetLiabilitiesResDTO> apiResponse = new ApiResponse<>();
        AssetLiabilitiesResDTO result = new AssetLiabilitiesResDTO();
        result.setCurrency(targetCurrency);

        List<OverviewSubResDTO> convertedAccounts =
                fetchAndConvertAccounts(userAndCorp, corpCifNo, targetCurrency);

        aggregateBySubProduct(result, convertedAccounts);
        zeroOutAllLiabilities(result);

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

        result.setLastUpdatedDate(OffsetDateTime.now().minusDays(1).format(DATETIME_FORMAT));

        apiResponse.setMsgBody(result);
        apiResponse.setMsgHeader(new ErrorResponse("0"));

        log.info("=== getDepositAssetLiabilities END | cif={} totalAsset={} {} ===",
                corpCifNo, totalAsset, targetCurrency);

        return apiResponse;
    }

    /**
     * Get detail overview untuk satu sub-type.
     */
    public OverviewResDTO getDepositSubTypeOverview(
            UserAndCorp userAndCorp, OvSubProductEnum subProduct, String baseCurrency) {

        String corpCifNo = corpCifResolver.resolveCif(userAndCorp.getCorpId());

        log.info("=== getDepositSubTypeOverview: {} | cif={} ===", subProduct, corpCifNo);

        OverviewResDTO overviewResDto = new OverviewResDTO();
        overviewResDto.setId(subProduct.name());
        overviewResDto.setTitle(getSubProductTitle(subProduct));
        overviewResDto.setProductAccType("assets");

        List<OverviewSubResDTO> accounts = fetchAccountsRaw(userAndCorp, corpCifNo);

        List<OverviewSubResDTO> filteredAccounts = accounts.stream()
                .filter(a -> a.getSubProduct() == subProduct)
                .collect(Collectors.toList());

        filteredAccounts.sort(null);
        overviewResDto.setAccount(filteredAccounts);
        overviewResDto.setLastUpdatedDate(OffsetDateTime.now().format(DATE_FORMAT));

        log.info("SubType {} - {} accounts found | cif={}",
                subProduct, filteredAccounts.size(), corpCifNo);
        return overviewResDto;
    }

    // ==================== PRIVATE ====================

    private String resolveTargetCurrency(String baseCurrency) {
        return (baseCurrency != null && !baseCurrency.isBlank())
                ? baseCurrency.trim().toUpperCase() : IDR;
    }

    /**
     * Ambil akun dari BTS lalu map ke DTO. Tanpa konversi currency.
     */
    private List<OverviewSubResDTO> fetchAccountsRaw(UserAndCorp userAndCorp, String corpCifNo) {
        List<DepositAcctSummarySpDTO> btsAccounts = fetchRawBtsAccounts(userAndCorp, corpCifNo);

        if (btsAccounts.isEmpty()) {
            log.warn("No accounts fetched from BTS | cif={}", corpCifNo);
            return new ArrayList<>();
        }

        log.info("Fetched {} accounts from BTS | cif={}", btsAccounts.size(), corpCifNo);
        return new ArrayList<>(accountMapper.mapAll(btsAccounts));
    }

    /**
     * Ambil akun dari BTS lalu konversi seluruhnya ke targetCurrency.
     */
    private List<OverviewSubResDTO> fetchAndConvertAccounts(
            UserAndCorp userAndCorp, String corpCifNo, String targetCurrency) {

        try {
            KursResolution kurs = kursResolver.resolve();

            if (kurs == null) {
                log.error("FALLBACK: kurs tidak tersedia dari Trapi maupun DB | cif={}. "
                        + "Total lintas currency TIDAK VALID.", corpCifNo);
                return fetchAccountsRaw(userAndCorp, corpCifNo);
            }

            Map<String, BigDecimal> rateMap = kurs.getRateMap();
            log.info("Kurs source={} pairs={} age={}h",
                    kurs.getSource(), rateMap.size(), kurs.getAgeHours());

            List<OverviewSubResDTO> mappedAccounts = fetchAccountsRaw(userAndCorp, corpCifNo);
            if (mappedAccounts.isEmpty()) {
                return mappedAccounts;
            }

            List<OverviewSubResDTO> convertedAccounts =
                    convertAllAccounts(mappedAccounts, targetCurrency, rateMap);

            log.info("Converted {} accounts to {} | cif={}",
                    convertedAccounts.size(), targetCurrency, corpCifNo);
            return convertedAccounts;

        } catch (Exception e) {
            log.error("Error in fetchAndConvertAccounts | cif={}: {}", corpCifNo, e.getMessage(), e);
            return fetchAccountsRaw(userAndCorp, corpCifNo);
        }
    }

    private List<DepositAcctSummarySpDTO> fetchRawBtsAccounts(UserAndCorp userAndCorp, String corpCifNo) {
        try {
            AccountListReq req = createAccountListRequest(corpCifNo);
            ApiResponse<DepositAcctListingDTO> response =
                    depositAllAccountListService.getAllAccounts(userAndCorp, req);

            if (response.getMsgBody() != null && response.getMsgBody().getDatas() != null) {
                return response.getMsgBody().getDatas().getData();
            }
        } catch (Exception e) {
            log.error("Error fetching BTS accounts | cif={}: {}", corpCifNo, e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    private List<OverviewSubResDTO> convertAllAccounts(
            List<OverviewSubResDTO> accounts, String targetCurrency,
            Map<String, BigDecimal> rateMap) {

        List<OverviewSubResDTO> converted = accounts.stream()
                .map(account -> convertSingleAccount(account, targetCurrency, rateMap))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        if (converted.size() < accounts.size()) {
            log.error("{} dari {} rekening gagal dikonversi ke {}. Total tidak lengkap.",
                    accounts.size() - converted.size(), accounts.size(), targetCurrency);
        }
        return converted;
    }

    /**
     * Konversi satu rekening. Null kalau rate tidak ditemukan
     */
    private OverviewSubResDTO convertSingleAccount(
            OverviewSubResDTO account, String targetCurrency, Map<String, BigDecimal> rateMap) {

        String originalCurrency = account.getCurrency();
        BigDecimal originalAmount = account.getTotalAmount() != null
                ? account.getTotalAmount() : BigDecimal.ZERO;

        if (originalAmount.signum() == 0) {
            account.setCurrency(targetCurrency);
            return account;
        }

        BigDecimal convertedAmount = graphConverter.convert(
                originalAmount, originalCurrency, targetCurrency, rateMap);

        if (convertedAmount == null) {
            log.error("Gagal konversi {} {} -> {} untuk rekening [{}]. Rekening dilewati.",
                    originalAmount, originalCurrency, targetCurrency, account.getLabel());
            return null;
        }

        account.setTotalAmount(convertedAmount);
        account.setCurrency(targetCurrency);
        return account;
    }

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
            OvSubProductEnum sub = acc.getSubProduct();

            if (sub == null) {
                totalOther = totalOther.add(amount);
                continue;
            }

            switch (sub) {
                case CA:   totalCA   = totalCA.add(amount);   break;
                case SA:   totalSA   = totalSA.add(amount);   break;
                case MFCA: totalMFCA = totalMFCA.add(amount); break;
                case FCA:  totalFCA  = totalFCA.add(amount);  break;
                case FD:   totalFD   = totalFD.add(amount);   break;
                case TD:   totalTD   = totalTD.add(amount);   break;
                case VA:   totalVA   = totalVA.add(amount);   break;
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
}