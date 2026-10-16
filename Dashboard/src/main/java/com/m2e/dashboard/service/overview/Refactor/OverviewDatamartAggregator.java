package com.m2e.dashboard.service.overview.Refactor;

import com.m2e.dashboard.dto.deposit.DepositFdStatsByCurrencyProjection;
import com.m2e.dashboard.dto.loan.LoanPbAmountByCurrencyProjection;
import com.m2e.dashboard.dto.loan.overdraft.LoanPrkStatsByCurrencyProjection;
import com.m2e.dashboard.dto.overview.CifAmountByCurrencyProjection;
import com.m2e.dashboard.dto.trade.TradeOutstandingByProductProjection;
import com.m2e.dashboard.enums.LoanPbProductEnum;
import com.m2e.dashboard.enums.OvSubProductEnum;
import com.m2e.dashboard.repository.DatamartAgreementRepository;
import com.m2e.dashboard.repository.DepositFdRepository;
import com.m2e.dashboard.repository.LoanPbDrawRepository;
import com.m2e.dashboard.repository.LoanPrkRepository;

import com.m2e.dashboard.service.overview.AssetGraph.GraphCurrencyConverter;
import com.m2e.dashboard.service.overview.Datamart.DatamartDateRange;
import com.m2e.dashboard.service.overview.Datamart.DatamartDateResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Satu-satunya tempat aturan FD asset dan liabilities overview.
 *
 * FD  : total pokok DATAMART_DEPOSIT_FD
 * TRD : seluruh outstanding trade
 * OD  : pemakaian PRK (CURRBAL negatif), sama dengan stats PRK
 * TL  : outstanding draw PB, sama dengan stats PB
 * RC  : outstanding draw PPB, sama dengan stats PPB
 *
 * Dua mode: satu CIF untuk /asset (gagal -> exception), dan seluruh CIF untuk
 * job snapshot (query gagal -> exception, konversi gagal per CIF -> CIF ditandai partial).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OverviewDatamartAggregator {

    private final DatamartAgreementRepository tradeRepository;
    private final DepositFdRepository fdRepository;
    private final LoanPrkRepository prkRepository;
    private final LoanPbDrawRepository pbDrawRepository;
    private final DatamartDateResolver dateResolver;
    private final GraphCurrencyConverter converter;

    public record AllCifResult(Map<String, BigDecimal> fdByCif,
                               Map<String, LiabilityBreakdown> liabilitiesByCif,
                               Set<String> partialCifs) {}

    // ======================= SATU CIF (/asset) =======================

    public BigDecimal fdAsset(String cifNo, String target, Map<String, BigDecimal> rateMap) {
        Optional<DatamartDateRange> range = dateResolver.fd();
        if (range.isEmpty()) {
            return BigDecimal.ZERO;
        }
        DatamartDateRange r = range.get();

        List<DepositFdStatsByCurrencyProjection> rows = query("deposito",
                () -> fdRepository.getStatsByCurrency(cifNo, r.startOfDay(), r.endOfDay()));

        BigDecimal total = BigDecimal.ZERO;
        for (DepositFdStatsByCurrencyProjection row : rows) {
            total = total.add(convert(row.getTotalPrincipal(), row.getCurrency(), target, rateMap));
        }
        return total;
    }

    public LiabilityBreakdown liabilities(String cifNo, String target, Map<String, BigDecimal> rateMap) {
        TradeTotals trade = trade(cifNo, target, rateMap);
        BigDecimal od = prk(cifNo, target, rateMap);
        BigDecimal tl = pb(cifNo, LoanPbProductEnum.PB, target, rateMap);
        BigDecimal rc = pb(cifNo, LoanPbProductEnum.PPB, target, rateMap);

        return new LiabilityBreakdown(trade.total(), trade.imp(), trade.exp(),
                trade.guarantee(), trade.financing(), od, tl, rc);
    }

    private record TradeTotals(BigDecimal total, BigDecimal imp, BigDecimal exp,
                               BigDecimal guarantee, BigDecimal financing) {}

    private TradeTotals trade(String cifNo, String target, Map<String, BigDecimal> rateMap) {
        Optional<DatamartDateRange> range = dateResolver.trade();
        BigDecimal z = BigDecimal.ZERO;
        if (range.isEmpty()) {
            return new TradeTotals(z, z, z, z, z);
        }
        DatamartDateRange r = range.get();

        List<TradeOutstandingByProductProjection> rows = query("trade",
                () -> tradeRepository.getAllOutstandingByProductAndCurrency(cifNo, r.startOfDay(), r.endOfDay()));

        BigDecimal total = z, imp = z, exp = z, guarantee = z, financing = z;

        for (TradeOutstandingByProductProjection row : rows) {
            BigDecimal converted = convert(row.getTotalOutstandingBal(), row.getCurrency(), target, rateMap);
            total = total.add(converted);

            OvSubProductEnum sub = OvSubProductEnum.fromDescription(row.getProductType());
            if (sub == null) {
                log.warn("[OVERVIEW] productType [{}] tidak ada di OvSubProductEnum. "
                        + "Masuk total trade tapi tidak masuk bucket.", row.getProductType());
                continue;
            }
            switch (sub.getTradeBucket()) {
                case IMPORT -> imp = imp.add(converted);
                case EXPORT -> exp = exp.add(converted);
                case GUARANTEE -> guarantee = guarantee.add(converted);
                case FINANCING -> financing = financing.add(converted);
                default -> log.warn("[OVERVIEW] Produk [{}] bukan produk trade", row.getProductType());
            }
        }
        return new TradeTotals(total, imp, exp, guarantee, financing);
    }

    private BigDecimal prk(String cifNo, String target, Map<String, BigDecimal> rateMap) {
        Optional<DatamartDateRange> range = dateResolver.prk();
        if (range.isEmpty()) {
            return BigDecimal.ZERO;
        }
        DatamartDateRange r = range.get();

        List<LoanPrkStatsByCurrencyProjection> rows = query("PRK",
                () -> prkRepository.getStatsByCurrency(cifNo, r.startOfDay(), r.endOfDay()));

        BigDecimal total = BigDecimal.ZERO;
        for (LoanPrkStatsByCurrencyProjection row : rows) {
            total = total.add(convert(row.getTotalUtilised(), row.getCurrency(), target, rateMap));
        }
        return total;
    }

    private BigDecimal pb(String cifNo, LoanPbProductEnum product, String target,
                          Map<String, BigDecimal> rateMap) {
        Optional<DatamartDateRange> range = dateResolver.pb();
        if (range.isEmpty()) {
            return BigDecimal.ZERO;
        }
        DatamartDateRange r = range.get();

        List<LoanPbAmountByCurrencyProjection> rows = query(product.name(),
                () -> pbDrawRepository.sumOutstandingByCurrency(
                        cifNo, product.getAccrueMethod(), r.startOfDay(), r.endOfDay()));

        BigDecimal total = BigDecimal.ZERO;
        for (LoanPbAmountByCurrencyProjection row : rows) {
            total = total.add(convert(row.getTotal(), row.getCurrency(), target, rateMap));
        }
        return total;
    }

    // ===================== SELURUH CIF (snapshot) =====================

    /** Semua nilai dalam IDR. */
    public AllCifResult loadAllCifs(Map<String, BigDecimal> rateMap) {
        Set<String> partial = new HashSet<>();
        Map<String, BigDecimal> fd = new HashMap<>();
        Map<String, BigDecimal> trd = new HashMap<>();
        Map<String, BigDecimal> od = new HashMap<>();
        Map<String, BigDecimal> tl = new HashMap<>();
        Map<String, BigDecimal> rc = new HashMap<>();

        dateResolver.fd().ifPresent(r -> accumulate(query("deposito",
                        () -> fdRepository.sumPrincipalGroupedByCif(r.startOfDay(), r.endOfDay())),
                rateMap, fd, partial, "FD"));

        dateResolver.trade().ifPresent(r -> accumulate(query("trade",
                        () -> tradeRepository.getAllOutstandingGroupedByCif(r.startOfDay(), r.endOfDay())
                                .stream()
                                .map(t -> new CifAmountByCurrencyProjection(
                                        t.getCifNo(), t.getCurrency(), t.getTotalOutstandingBal()))
                                .collect(Collectors.toList())),
                rateMap, trd, partial, "TRD"));

        dateResolver.prk().ifPresent(r -> accumulate(query("PRK",
                        () -> prkRepository.sumUtilisedGroupedByCif(r.startOfDay(), r.endOfDay())),
                rateMap, od, partial, "OD"));

        dateResolver.pb().ifPresent(r -> {
            accumulate(query("PB", () -> pbDrawRepository.sumOutstandingGroupedByCif(
                            LoanPbProductEnum.PB.getAccrueMethod(), r.startOfDay(), r.endOfDay())),
                    rateMap, tl, partial, "TL");
            accumulate(query("PPB", () -> pbDrawRepository.sumOutstandingGroupedByCif(
                            LoanPbProductEnum.PPB.getAccrueMethod(), r.startOfDay(), r.endOfDay())),
                    rateMap, rc, partial, "RC");
        });

        Set<String> cifs = new HashSet<>();
        cifs.addAll(trd.keySet());
        cifs.addAll(od.keySet());
        cifs.addAll(tl.keySet());
        cifs.addAll(rc.keySet());

        Map<String, LiabilityBreakdown> liabilities = new HashMap<>();
        for (String cif : cifs) {
            liabilities.put(cif, LiabilityBreakdown.totalsOnly(
                    trd.get(cif), od.get(cif), tl.get(cif), rc.get(cif)));
        }

        log.info("[OVERVIEW] Datamart seluruh CIF: FD={} CIF, liabilities={} CIF, partial={} CIF",
                fd.size(), liabilities.size(), partial.size());

        return new AllCifResult(fd, liabilities, partial);
    }

    private void accumulate(List<CifAmountByCurrencyProjection> rows, Map<String, BigDecimal> rateMap,
                            Map<String, BigDecimal> sink, Set<String> partial, String label) {
        for (CifAmountByCurrencyProjection row : rows) {
            BigDecimal amount = row.getTotal() != null ? row.getTotal() : BigDecimal.ZERO;
            if (amount.signum() == 0) {
                continue;
            }
            BigDecimal idr = converter.toIdr(amount, row.getCurrency(), rateMap);
            if (idr == null) {
                partial.add(row.getCifNo());
                log.warn("[OVERVIEW] {} cif={}: kurs {} ke IDR tidak ditemukan, CIF ditandai partial",
                        label, row.getCifNo(), row.getCurrency());
                continue;
            }
            sink.merge(row.getCifNo(), idr, BigDecimal::add);
        }
    }

    // ============================ HELPER ============================

    private <T> T query(String label, Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (Exception e) {
            log.error("[OVERVIEW] Query {} gagal: {}", label, e.getMessage(), e);
            throw new OverviewSourceException("Data " + label + " tidak dapat diproses saat ini");
        }
    }

    private BigDecimal convert(BigDecimal amount, String from, String to, Map<String, BigDecimal> rateMap) {
        BigDecimal value = amount != null ? amount : BigDecimal.ZERO;
        if (value.signum() == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal converted = converter.convert(value, from, to, rateMap);
        if (converted == null) {
            throw new OverviewSourceException("Kurs " + (from != null ? from : "(kosong)")
                    + " ke " + to + " tidak ditemukan");
        }
        return converted;
    }
}
