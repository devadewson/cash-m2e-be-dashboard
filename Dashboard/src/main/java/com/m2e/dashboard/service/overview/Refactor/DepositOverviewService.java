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
import java.util.stream.Collectors;

/**
 * Deposit overview.
 *
 * Listing (/overview, /overview/{subProduct}) — nilai BTS apa adanya.
 *
 * Summary (/asset):
 *   Asset       = CA + FCA + SA (BTS) + FD (DATAMART_DEPOSIT_FD)
 *   Liabilities = Trade + PRK + PB + PPB (datamart, tanggal muat terakhir)
 *   Semua dikonversi ke baseCurrency dengan kurs yang sama.
 *   Satu sumber gagal -> seluruh response error.
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
    private final OverviewDatamartAggregator datamartAggregator;

    private static final String IDR = "IDR";
    private static final String CODE_SUCCESS = "0";
    private static final String CODE_FAILED = "1";
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MMM-yyyy");
    private static final DateTimeFormatter DATETIME_FORMAT =
            DateTimeFormatter.ofPattern("dd MMM yyyy, HH:mm:ss '(GMT+8)'");

    // ============================ LISTING ============================

    public OverviewResDTO getDepositOverview(UserAndCorp userAndCorp, String baseCurrency) {

        String corpCifNo = corpCifResolver.resolveCif(userAndCorp.getCorpId());

        log.info("=== getDepositOverview START | user={} corpId={} cif={} ===",
                userAndCorp.getUserId(), userAndCorp.getCorpId(), corpCifNo);

        OverviewResDTO overviewResDto = new OverviewResDTO();
        overviewResDto.setId(OverviewEnum.DEPOSIT.name());
        overviewResDto.setTitle(OverviewEnum.DEPOSIT.getDescription());
        overviewResDto.setProductAccType(OverviewEnum.DEPOSIT.getTab());

        List<OverviewSubResDTO> accounts = new ArrayList<>(fetchAccountsRaw(userAndCorp, corpCifNo));

        accounts.sort(null);
        overviewResDto.setAccount(accounts);
        overviewResDto.setLastUpdatedDate(OffsetDateTime.now().format(DATE_FORMAT));

        log.info("=== getDepositOverview END | cif={} accounts={} ===", corpCifNo, accounts.size());
        return overviewResDto;
    }

    public OverviewResDTO getDepositSubTypeOverview(
            UserAndCorp userAndCorp, OvSubProductEnum subProduct, String baseCurrency) {

        String corpCifNo = corpCifResolver.resolveCif(userAndCorp.getCorpId());

        log.info("=== getDepositSubTypeOverview: {} | cif={} ===", subProduct, corpCifNo);

        OverviewResDTO overviewResDto = new OverviewResDTO();
        overviewResDto.setId(subProduct.name());
        overviewResDto.setTitle(getSubProductTitle(subProduct));
        overviewResDto.setProductAccType("assets");

        List<OverviewSubResDTO> filteredAccounts = fetchAccountsRaw(userAndCorp, corpCifNo).stream()
                .filter(a -> a.getSubProduct() == subProduct)
                .collect(Collectors.toList());

        filteredAccounts.sort(null);
        overviewResDto.setAccount(filteredAccounts);
        overviewResDto.setLastUpdatedDate(OffsetDateTime.now().format(DATE_FORMAT));

        log.info("SubType {} - {} accounts found | cif={}", subProduct, filteredAccounts.size(), corpCifNo);
        return overviewResDto;
    }

    // ======================= ASSET & LIABILITIES =======================

    public ApiResponse<AssetLiabilitiesResDTO> getDepositAssetLiabilities(
            UserAndCorp userAndCorp, String baseCurrency) {

        String target = resolveTargetCurrency(baseCurrency);

        String cifNo;
        try {
            cifNo = corpCifResolver.resolveCif(userAndCorp.getCorpId());
        } catch (Exception e) {
            log.error("[ASSET] CIF gagal di-resolve corpId={}: {}", userAndCorp.getCorpId(), e.getMessage());
            return failure("CIF tidak ditemukan untuk korporasi ini");
        }

        log.info("=== getDepositAssetLiabilities START | cif={} currency={} ===", cifNo, target);

        try {
            // Kurs diambil sekali, dipakai untuk seluruh asset dan liabilities
            KursResolution kurs = kursResolver.resolve();
            Map<String, BigDecimal> rateMap = (kurs != null) ? kurs.getRateMap() : Map.of();
            if (kurs == null) {
                log.warn("[ASSET] Kurs tidak tersedia | cif={}. Hanya currency {} yang bisa dihitung.",
                        cifNo, target);
            } else {
                log.info("[ASSET] Kurs source={} pairs={} age={}h",
                        kurs.getSource(), rateMap.size(), kurs.getAgeHours());
            }

            // ===== ASSET =====
            BtsAssets bts = sumBtsAssets(fetchAccountsStrict(userAndCorp, cifNo), target, rateMap);
            BigDecimal fd = datamartAggregator.fdAsset(cifNo, target, rateMap);
            BigDecimal totalAsset = bts.ca().add(bts.fca()).add(bts.sa()).add(fd);

            // ===== LIABILITIES =====
            LiabilityBreakdown liab = datamartAggregator.liabilities(cifNo, target, rateMap);
            BigDecimal totalLiabilities = liab.total();

            AssetLiabilitiesResDTO result = new AssetLiabilitiesResDTO();
            result.setCurrency(target);

            result.setTotalCA(bts.ca());
            result.setTotalFCA(bts.fca());
            result.setTotalSA(bts.sa());
            result.setTotalFD(fd);
            // Di luar rumus asset; dinolkan supaya rincian selalu sama dengan totalnya
            result.setTotalTD(BigDecimal.ZERO);
            result.setTotalMFCA(BigDecimal.ZERO);
            result.setTotalVA(BigDecimal.ZERO);
            result.setTotalDpOther(BigDecimal.ZERO);
            result.setTotalInv(BigDecimal.ZERO);

            result.setTotalTRD(liab.trd());
            result.setTotalTRDI(liab.trdImport());
            result.setTotalTRDE(liab.trdExport());
            result.setTotalTRDG(liab.trdGuarantee());
            result.setTotalTRDL(liab.trdFinancing());
            result.setTotalOD(liab.od());
            result.setTotalTL(liab.tl());
            result.setTotalRc(liab.rc());
            zeroOutOtherLiabilities(result);

            result.setTotalAsset(totalAsset);
            result.setTotalLiabilities(totalLiabilities);
            result.setTotalAssetRatio(totalAsset);
            result.setTotalLiabilitiesRatio(totalLiabilities);
            result.setWorkingRatio(BigDecimal.ZERO);
            result.setQuickRatio(BigDecimal.ZERO);
            result.setLastUpdatedDate(OffsetDateTime.now().minusDays(1).format(DATETIME_FORMAT));

            log.info("=== getDepositAssetLiabilities END | cif={} asset={} (CA={} FCA={} SA={} FD={}) "
                            + "liabilities={} (TRD={} OD={} TL={} RC={}) {} ===",
                    cifNo, totalAsset, bts.ca(), bts.fca(), bts.sa(), fd,
                    totalLiabilities, liab.trd(), liab.od(), liab.tl(), liab.rc(), target);

            return success(result);

        } catch (OverviewSourceException e) {
            log.error("[ASSET] {} | cif={}", e.getMessage(), cifNo);
            return failure(e.getMessage());
        } catch (Exception e) {
            log.error("[ASSET] FAILED cif={}: {}", cifNo, e.getMessage(), e);
            return failure("Data tidak dapat diproses saat ini");
        }
    }

    private record BtsAssets(BigDecimal ca, BigDecimal fca, BigDecimal sa) {}

    /**
     * Hanya CA, FCA, dan SA dari BTS. FD/TD dari BTS dikeluarkan karena FD
     * diambil dari datamart;
     */
    private BtsAssets sumBtsAssets(List<OverviewSubResDTO> accounts, String target,
                                   Map<String, BigDecimal> rateMap) {
        BigDecimal ca = BigDecimal.ZERO;
        BigDecimal fca = BigDecimal.ZERO;
        BigDecimal sa = BigDecimal.ZERO;
        int excluded = 0;

        for (OverviewSubResDTO acc : accounts) {
            OvSubProductEnum sub = acc.getSubProduct();
            if (sub != OvSubProductEnum.CA && sub != OvSubProductEnum.FCA && sub != OvSubProductEnum.SA) {
                excluded++;
                continue;
            }

            BigDecimal amount = acc.getTotalAmount() != null ? acc.getTotalAmount() : BigDecimal.ZERO;
            if (amount.signum() == 0) {
                continue;
            }

            BigDecimal converted = graphConverter.convert(amount, acc.getCurrency(), target, rateMap);
            if (converted == null) {
                throw new OverviewSourceException("Kurs " + acc.getCurrency() + " ke " + target + " tidak ditemukan");
            }

            switch (sub) {
                case CA -> ca = ca.add(converted);
                case FCA -> fca = fca.add(converted);
                case SA -> sa = sa.add(converted);
                default -> { }
            }
        }

        if (excluded > 0) {
            log.debug("[ASSET] {} rekening BTS di luar CA/FCA/SA tidak dihitung", excluded);
        }
        return new BtsAssets(ca, fca, sa);
    }

    /** Untuk /asset: kegagalan BTS menjadi error, bukan daftar kosong. */
    private List<OverviewSubResDTO> fetchAccountsStrict(UserAndCorp userAndCorp, String cifNo) {
        ApiResponse<DepositAcctListingDTO> response;
        try {
            response = depositAllAccountListService.getAllAccounts(userAndCorp, createAccountListRequest(cifNo));
        } catch (Exception e) {
            log.error("[ASSET] BTS gagal | cif={}: {}", cifNo, e.getMessage(), e);
            throw new OverviewSourceException("Data rekening deposito tidak dapat diambil saat ini");
        }

        if (response == null || response.getMsgBody() == null) {
            throw new OverviewSourceException("Data rekening deposito tidak dapat diambil saat ini");
        }
        if (response.getMsgBody().getDatas() == null || response.getMsgBody().getDatas().getData() == null) {
            return List.of();
        }
        return accountMapper.mapAll(response.getMsgBody().getDatas().getData());
    }

    // ============================ HELPER ============================

    private String resolveTargetCurrency(String baseCurrency) {
        return (baseCurrency != null && !baseCurrency.isBlank())
                ? baseCurrency.trim().toUpperCase() : IDR;
    }

    /** Untuk listing: kegagalan BTS menghasilkan daftar kosong. */
    private List<OverviewSubResDTO> fetchAccountsRaw(UserAndCorp userAndCorp, String corpCifNo) {
        List<DepositAcctSummarySpDTO> btsAccounts = new ArrayList<>();
        try {
            ApiResponse<DepositAcctListingDTO> response =
                    depositAllAccountListService.getAllAccounts(userAndCorp, createAccountListRequest(corpCifNo));
            if (response.getMsgBody() != null && response.getMsgBody().getDatas() != null
                    && response.getMsgBody().getDatas().getData() != null) {
                btsAccounts = response.getMsgBody().getDatas().getData();
            }
        } catch (Exception e) {
            log.error("Error fetching BTS accounts | cif={}: {}", corpCifNo, e.getMessage(), e);
        }

        if (btsAccounts.isEmpty()) {
            log.warn("No accounts fetched from BTS | cif={}", corpCifNo);
            return new ArrayList<>();
        }
        return new ArrayList<>(accountMapper.mapAll(btsAccounts));
    }

    private AccountListReq createAccountListRequest(String corpCifNo) {
        AccountListReq req = new AccountListReq();
        req.setCorpCifNo(corpCifNo);
        req.setRealTimeIndicator(Boolean.TRUE);
        req.setPageSize(Integer.MAX_VALUE);
        return req;
    }

    /** Liabilities tanpa sumber data, tetap nol. */
    private void zeroOutOtherLiabilities(AssetLiabilitiesResDTO dto) {
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

    private ApiResponse<AssetLiabilitiesResDTO> success(AssetLiabilitiesResDTO body) {
        ApiResponse<AssetLiabilitiesResDTO> res = new ApiResponse<>();
        res.setMsgHeader(new ErrorResponse(CODE_SUCCESS));
        res.setMsgBody(body);
        return res;
    }

    private ApiResponse<AssetLiabilitiesResDTO> failure(String message) {
        ErrorResponse header = new ErrorResponse(CODE_FAILED);
        header.setErrorMessage(message);
        ApiResponse<AssetLiabilitiesResDTO> res = new ApiResponse<>();
        res.setMsgHeader(header);
        res.setMsgBody(null);
        return res;
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