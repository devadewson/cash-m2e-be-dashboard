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
import com.m2e.dashboard.dto.trade.TradeOutstandingByProductProjection;
import com.m2e.dashboard.enums.OvSubProductEnum;
import com.m2e.dashboard.enums.OverviewEnum;
import com.m2e.dashboard.repository.DatamartAgreementRepository;
import com.m2e.dashboard.service.deposit.ca.Rest.DepositAllAccountListService;
import com.m2e.dashboard.service.overview.AssetGraph.GraphCurrencyConverter;
import com.m2e.dashboard.service.overview.JobKursService.KursResolver;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
 * Listing (/overview, /overview/{subProduct}) mengembalikan nilai apa adanya
 * dari BTS, tiap rekening membawa currency masing-masing.
 *
 * Summary (/asset) menjumlahkan lintas rekening dan lintas produk, jadi:
 * - asset diambil dari deposit (BTS)
 * - liabilities diambil dari outstanding trade (DATAMART_AGREEMENT)
 * - keduanya dikonversi ke targetCurrency memakai kurs yang sama
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
    private final DatamartAgreementRepository datamartAgreementRepository;

    private static final String IDR = "IDR";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss '(GMT+8)'");

    /**
     * Get all deposit accounts. Nilai apa adanya dari BTS, tanpa konversi.
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
     * Asset dari deposit (BTS), liabilities dari outstanding trade (DATAMART).
     * Keduanya dikonversi ke targetCurrency dengan kurs yang sama.
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

        // Kurs diambil sekali, dipakai untuk deposit dan trade
        KursResolution kurs = kursResolver.resolve();
        Map<String, BigDecimal> rateMap = (kurs != null) ? kurs.getRateMap() : Map.of();

        if (kurs == null) {
            log.error("Kurs tidak tersedia dari Trapi maupun DB | cif={}. "
                    + "Total lintas currency TIDAK VALID.", corpCifNo);
        } else {
            log.info("Kurs source={} pairs={} age={}h",
                    kurs.getSource(), rateMap.size(), kurs.getAgeHours());
        }

        // ===== ASSET: deposit dari BTS =====
        List<OverviewSubResDTO> mappedAccounts = fetchAccountsRaw(userAndCorp, corpCifNo);
        List<OverviewSubResDTO> convertedAccounts = (kurs != null)
                ? convertAllAccounts(mappedAccounts, targetCurrency, rateMap)
                : mappedAccounts;

        aggregateBySubProduct(result, convertedAccounts);
        zeroOutNonTradeLiabilities(result);

        BigDecimal totalAsset = result.getTotalCA()
                .add(result.getTotalMFCA())
                .add(result.getTotalFCA())
                .add(result.getTotalFD())
                .add(result.getTotalTD())
                .add(result.getTotalVA())
                .add(result.getTotalDpOther());

        // ===== LIABILITIES: outstanding trade dari DATAMART =====
        BigDecimal totalTrade = aggregateTrade(result, corpCifNo, targetCurrency, rateMap);

        result.setTotalAsset(totalAsset);
        result.setTotalLiabilities(totalTrade);
        result.setTotalAssetRatio(totalAsset);
        result.setTotalLiabilitiesRatio(totalTrade);
        result.setWorkingRatio(BigDecimal.ZERO);
        result.setQuickRatio(BigDecimal.ZERO);

        result.setLastUpdatedDate(OffsetDateTime.now().minusDays(1).format(DATETIME_FORMAT));

        apiResponse.setMsgBody(result);
        apiResponse.setMsgHeader(new ErrorResponse("0"));

        log.info("=== getDepositAssetLiabilities END | cif={} asset={} liabilities={} {} ===",
                corpCifNo, totalAsset, totalTrade, targetCurrency);

        return apiResponse;
    }

    /**
     * Get detail overview untuk satu sub-type. Nilai apa adanya dari BTS.
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

    // ==================== DEPOSIT ====================

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
     * Konversi satu rekening. Null kalau rate tidak ditemukan,
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
    }

    // ==================== TRADE ====================

    private BigDecimal aggregateTrade(AssetLiabilitiesResDTO result, String cifNo,
                                      String targetCurrency, Map<String, BigDecimal> rateMap) {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        try {
            List<TradeOutstandingByProductProjection> rows =
                    datamartAgreementRepository.getAllOutstandingByProductAndCurrency(
                            cifNo, startOfDay, endOfDay);

            if (rows.isEmpty()) {
                log.warn("Tidak ada data trade untuk cif={} tanggal={}. "
                        + "Cek apakah job SFTP sudah jalan hari ini.", cifNo, today);
                setTradeTotals(result, BigDecimal.ZERO, BigDecimal.ZERO,
                        BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
                return BigDecimal.ZERO;
            }

            BigDecimal total = BigDecimal.ZERO;
            BigDecimal trdImport = BigDecimal.ZERO;
            BigDecimal trdExport = BigDecimal.ZERO;
            BigDecimal trdGuarantee = BigDecimal.ZERO;
            BigDecimal trdLoan = BigDecimal.ZERO;

            for (TradeOutstandingByProductProjection row : rows) {
                BigDecimal amount = row.getTotalOutstandingBal() != null
                        ? row.getTotalOutstandingBal() : BigDecimal.ZERO;

                if (amount.signum() == 0) {
                    continue;
                }

                BigDecimal converted = graphConverter.convert(
                        amount, row.getCurrency(), targetCurrency, rateMap);

                if (converted == null) {
                    log.error("Gagal konversi trade {} {} -> {} pada produk [{}]. Nilai dilewati.",
                            amount, row.getCurrency(), targetCurrency, row.getProductType());
                    continue;
                }

                total = total.add(converted);

                OvSubProductEnum sub = OvSubProductEnum.fromDescription(row.getProductType());
                if (sub == null) {
                    log.warn("productType [{}] tidak ada di OvSubProductEnum. "
                            + "Masuk grand total tapi tidak masuk bucket.", row.getProductType());
                    continue;
                }

                switch (sub.getTradeBucket()) {
                    case IMPORT:    trdImport    = trdImport.add(converted);    break;
                    case EXPORT:    trdExport    = trdExport.add(converted);    break;
                    case GUARANTEE: trdGuarantee = trdGuarantee.add(converted); break;
                    case FINANCING: trdLoan      = trdLoan.add(converted);      break;
                    default:
                        log.warn("Produk [{}] bukan produk trade, tidak masuk bucket mana pun",
                                row.getProductType());
                        break;
                }
            }

            setTradeTotals(result, total, trdImport, trdExport, trdGuarantee, trdLoan);

            log.info("Trade cif={} total={} (I={} E={} G={} L={}) {}",
                    cifNo, total, trdImport, trdExport, trdGuarantee, trdLoan, targetCurrency);

            return total;

        } catch (Exception e) {
            log.error("Gagal agregasi trade | cif={}: {}", cifNo, e.getMessage(), e);
            setTradeTotals(result, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
            return BigDecimal.ZERO;
        }
    }

    private void setTradeTotals(AssetLiabilitiesResDTO result, BigDecimal total,
                                BigDecimal trdI, BigDecimal trdE,
                                BigDecimal trdG, BigDecimal trdL) {
        result.setTotalTRD(total);
        result.setTotalTRDI(trdI);
        result.setTotalTRDE(trdE);
        result.setTotalTRDG(trdG);
        result.setTotalTRDL(trdL);
    }

    // ==================== HELPER ====================

    private AccountListReq createAccountListRequest(String corpCifNo) {
        AccountListReq req = new AccountListReq();
        req.setCorpCifNo(corpCifNo);
        req.setRealTimeIndicator(Boolean.TRUE);
        req.setPageSize(Integer.MAX_VALUE);
        return req;
    }

    /** Liabilities selain trade masih nol sampai produk loan dan cards ditambahkan. */
    private void zeroOutNonTradeLiabilities(AssetLiabilitiesResDTO dto) {
        dto.setTotalOD(BigDecimal.ZERO);
        dto.setTotalTL(BigDecimal.ZERO);
        dto.setTotalRc(BigDecimal.ZERO);
        dto.setTotalCrc(BigDecimal.ZERO);
        dto.setTotalChc(BigDecimal.ZERO);
        dto.setTotalDc(BigDecimal.ZERO);
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