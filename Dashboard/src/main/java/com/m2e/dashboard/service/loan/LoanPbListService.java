package com.m2e.dashboard.service.loan;


import com.m2e.dashboard.config.CorpCifResolver;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.loan.LNAcctListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.loan.tlf.LoanPbDrawDTO;
import com.m2e.dashboard.dto.loan.tlf.LoanPbFacilityDTO;
import com.m2e.dashboard.dto.loan.tlf.LoanPbFacilityListingDTO;
import com.m2e.dashboard.enums.LoanPbProductEnum;
import com.m2e.dashboard.projection.LoanPbDrawListingProjection;
import com.m2e.dashboard.projection.LoanPbFacilityListingProjection;
import com.m2e.dashboard.repository.LoanPbDrawRepository;
import com.m2e.dashboard.repository.LoanPbFacilityRepository;
import com.m2e.dashboard.service.loan.jobDataMart.LoanPbCacheNames;
import com.m2e.dashboard.service.loan.jobDataMart.LoanPbDataDateResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Account list PB/PPB: fasilitas berpaginasi, tiap fasilitas berisi seluruh draw-nya.
 *
 * Selalu dua query: satu halaman fasilitas, lalu seluruh draw untuk fasilitas di halaman itu.
 * Nilai dikirim apa adanya, tanpa konversi currency.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoanPbListService {

    private final LoanPbFacilityRepository facilityRepository;
    private final LoanPbDrawRepository drawRepository;
    private final LoanPbDataDateResolver dataDateResolver;
    private final CorpCifResolver corpCifResolver;

    private static final String CODE_SUCCESS = "0";
    private static final String CODE_FAILED = "1";
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    private static final DateTimeFormatter EDW_DATE_FMT = DateTimeFormatter.BASIC_ISO_DATE; // yyyyMMdd
    private static final DateTimeFormatter DATE_FMT     = DateTimeFormatter.ofPattern("yyyy-MM-dd");


    @Cacheable(
            value = LoanPbCacheNames.LISTING,
            key = "#user.corpId + '_' + #product.name() + '_' + #req.page + '_' + #req.pageSize + '_' "
                    + "+ T(java.time.LocalDate).now()",
            unless = "#result == null || #result.msgBody == null"
    )
    public ApiResponse<LoanPbFacilityListingDTO> getAccountList(UserAndCorp user,
                                                                LoanPbProductEnum product,
                                                                LNAcctListReq req) {
        String accrueMethod = product.getAccrueMethod();

        String cifNo;
        try {
            cifNo = corpCifResolver.resolveCif(user.getCorpId());
        } catch (Exception e) {
            log.error("[{}-List] CIF gagal di-resolve corpId={}: {}",
                    product, user.getCorpId(), e.getMessage());
            return failure("CIF tidak ditemukan untuk korporasi ini");
        }

        try {
            // FE mengirim page mulai 0, sesuai PageRequest
            Pageable pageable = toPageable(req.getPage(), req.getPageSize());

            Optional<LoanPbDataDateResolver.DataRange> range = dataDateResolver.resolveLatest();
            if (range.isEmpty()) {
                log.warn("[{}-List] Belum ada data PB/PPB di database", product);
                return success(buildListing(cifNo, Page.empty(pageable)));
            }
            LoanPbDataDateResolver.DataRange r = range.get();

            log.info("[{}-List] START cif={} page={} pageSize={} dataDate={}",
                    product, cifNo, pageable.getPageNumber(), pageable.getPageSize(),
                    r.startOfDay().toLocalDate());

            // Query 1: satu halaman fasilitas
            Page<LoanPbFacilityListingProjection> facilityPage = facilityRepository.getFacilityPage(
                    cifNo, accrueMethod, r.startOfDay(), r.endOfDay(), pageable);

            // Query 2: seluruh draw untuk fasilitas di halaman ini
            List<String> noteNos = facilityPage.getContent().stream()
                    .map(LoanPbFacilityListingProjection::getNoteNo)
                    .collect(Collectors.toList());

            Map<String, List<LoanPbDrawDTO>> drawsByNote = noteNos.isEmpty()
                    ? Map.of()
                    : drawRepository.findDraws(cifNo, accrueMethod, noteNos, r.startOfDay(), r.endOfDay())
                    .stream()
                    .collect(Collectors.groupingBy(
                            LoanPbDrawListingProjection::getNoteNo,
                            LinkedHashMap::new,
                            Collectors.mapping(this::toDrawDto, Collectors.toList())));

            Page<LoanPbFacilityDTO> mapped = facilityPage.map(f ->
                    toFacilityDto(f, drawsByNote.getOrDefault(f.getNoteNo(), List.of())));

            LoanPbFacilityListingDTO body = buildListing(cifNo, mapped);

            log.info("[{}-List] SUCCESS cif={} facilitiesOnPage={} totalFacilities={}",
                    product, cifNo, mapped.getNumberOfElements(), mapped.getTotalElements());

            return success(body);

        } catch (Exception e) {
            log.error("[{}-List] FAILED cif={}: {}", product, cifNo, e.getMessage(), e);
            return failure("Data tidak dapat diproses saat ini");
        }
    }

    private Pageable toPageable(Integer page, Integer pageSize) {
        int p = (page == null || page < 0) ? 0 : page;
        int size = (pageSize == null || pageSize <= 0) ? DEFAULT_PAGE_SIZE : Math.min(pageSize, MAX_PAGE_SIZE);
        return PageRequest.of(p, size);
    }

    private LoanPbFacilityListingDTO buildListing(String cifNo, Page<LoanPbFacilityDTO> page) {
        LoanPbFacilityListingDTO dto = new LoanPbFacilityListingDTO();
        dto.setCorpCifNo(cifNo);
        dto.setDatas(new SearchCollectionResponse<>(page));
        return dto;
    }

    private LoanPbFacilityDTO toFacilityDto(LoanPbFacilityListingProjection f, List<LoanPbDrawDTO> draws) {
        LoanPbFacilityDTO dto = new LoanPbFacilityDTO();
        dto.setNoteNo(f.getNoteNo());
        dto.setCurrency(f.getCurrency());
        dto.setOriginalLimit(plain(f.getPlafond()));
        dto.setAvailableLimit(plain(f.getAvailableLimit()));
        dto.setAvailablePeriod(toDisplayDate(f.getAvailablePeriod()));

        // Draw tidak dipaginasi di server: satu halaman berisi seluruh draw fasilitas
        SearchCollectionResponse<LoanPbDrawDTO> drawCollection =
                new SearchCollectionResponse<>(1, 1, -1, draws.size());
        drawCollection.setData(new ArrayList<>(draws));
        dto.setDraws(drawCollection);
        return dto;
    }

    private LoanPbDrawDTO toDrawDto(LoanPbDrawListingProjection d) {
        LoanPbDrawDTO dto = new LoanPbDrawDTO();
        dto.setDrawdownNumber(nullToEmpty(d.getNoteNo()) + nullToEmpty(d.getDrawNo()));
        dto.setDrawDate(d.getDrawDate());
        dto.setCurrency(d.getCurrency());
        dto.setOutstanding(plain(d.getOutstanding()));
        dto.setMaturityDate(d.getMaturityDate());
        dto.setNextPaymentDue(plain(d.getNextPaymentDue()));
        dto.setNextPaymentDate(d.getNextPaymentDate());
        return dto;
    }

    private String plain(BigDecimal value) {
        return (value != null)
                ? value.setScale(2, RoundingMode.HALF_UP).toPlainString()
                : "0.00";
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    private ApiResponse<LoanPbFacilityListingDTO> success(LoanPbFacilityListingDTO body) {
        ApiResponse<LoanPbFacilityListingDTO> res = new ApiResponse<>();
        res.setMsgHeader(new ErrorResponse(CODE_SUCCESS));
        res.setMsgBody(body);
        return res;
    }

    private ApiResponse<LoanPbFacilityListingDTO> failure(String message) {
        ErrorResponse header = new ErrorResponse(CODE_FAILED);
        header.setErrorMessage(message);
        ApiResponse<LoanPbFacilityListingDTO> res = new ApiResponse<>();
        res.setMsgHeader(header);
        res.setMsgBody(null);
        return res;
    }

    //Format tanggal available periode
    private static String toDisplayDate(String raw) {
        if (raw == null || raw.isBlank()) {
            return raw;
        }
        String value = raw.trim();
        try {
            return LocalDate.parse(value, EDW_DATE_FMT).format(DATE_FMT);
        } catch (DateTimeParseException e) {
            return value;
        }
    }
}
