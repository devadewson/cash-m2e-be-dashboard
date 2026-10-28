package com.m2e.dashboard.service.deposit.fd.Rest;

import com.m2e.dashboard.config.CorpCifResolver;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.AccountListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAcctListingDTO;
import com.m2e.dashboard.repository.DepositFdListingProjection;
import com.m2e.dashboard.repository.DepositFdRepository;
import com.m2e.dashboard.service.overview.Datamart.DatamartDateRange;
import com.m2e.dashboard.service.overview.Datamart.DatamartDateResolver;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * Listing deposito berjangka dari DATAMART_DEPOSIT_FD, tanggal muat terakhir.
 * Nilai apa adanya, tanpa konversi currency.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DepositFDAccountListService {

    private final DepositFdRepository repository;
    private final CorpCifResolver corpCifResolver;
    private final DatamartDateResolver dateResolver;

    private static final String CODE_SUCCESS = "0";
    private static final String CODE_FAILED = "1";
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Cacheable(
            value = "deposit-fd-account-list",
            key = "#user.corpId + '_' + #req.page + '_' + #req.pageSize + '_' "
                    + "+ T(java.time.LocalDate).now()",
            unless = "#result == null || #result.msgBody == null"
    )
    public ApiResponse<DepositAcctListingDTO> getAccountList(UserAndCorp user, AccountListReq req) {

        String cifNo;
        try {
            cifNo = corpCifResolver.resolveCif(user.getCorpId());
        } catch (Exception e) {
            log.error("[FD-List] CIF gagal di-resolve corpId={}: {}", user.getCorpId(), e.getMessage());
            return failure("CIF tidak ditemukan untuk korporasi ini");
        }

        try {
            // FE mengirim page mulai 0, sesuai PageRequest
            Pageable pageable = toPageable(req.getPage(), req.getPageSize());

            Page<DepositAcctSummarySpDTO> mappedPage;
            Optional<DatamartDateRange> range = dateResolver.fd();

            if (range.isEmpty()) {
                log.warn("[FD-List] Belum ada data FD di database");
                mappedPage = Page.empty(pageable);
            } else {
                DatamartDateRange r = range.get();

                log.info("[FD-List] START cif={} page={} pageSize={} dataDate={}",
                        cifNo, pageable.getPageNumber(), pageable.getPageSize(), r.dataDate());

                Page<DepositFdListingProjection> resultPage =
                        repository.getListing(cifNo, r.startOfDay(), r.endOfDay(), pageable);

                mappedPage = resultPage.map(this::toDto);
            }

            SearchCollectionResponse<DepositAcctSummarySpDTO> result = new SearchCollectionResponse<>(mappedPage);

            DepositAcctListingDTO dto = new DepositAcctListingDTO();
            dto.setCorpCifNo(cifNo);
            dto.setDatas(result);

            log.info("[FD-List] SUCCESS cif={} page={} totalRecords={}",
                    cifNo, pageable.getPageNumber(), result.getTotalRecordCount());

            return success(dto);

        } catch (Exception e) {
            log.error("[FD-List] FAILED cif={}: {}", cifNo, e.getMessage(), e);
            return failure("Data deposito tidak dapat diproses saat ini");
        }
    }

    private Pageable toPageable(Integer page, Integer pageSize) {
        int p = (page == null || page < 0) ? 0 : page;
        int size = (pageSize == null || pageSize <= 0) ? DEFAULT_PAGE_SIZE : pageSize;
        return PageRequest.of(p, size);
    }

    private DepositAcctSummarySpDTO toDto(DepositFdListingProjection p) {
        DepositAcctSummarySpDTO dto = new DepositAcctSummarySpDTO();
        dto.setAcctNumber(p.getDepositNumber());
        dto.setCurrency(p.getCurrency());
        dto.setAcctType("TD");

        String principal = (p.getPrincipal() != null)
                ? p.getPrincipal().setScale(2, RoundingMode.HALF_UP).toPlainString()
                : "0.00";

        dto.setPrincipal(principal);
        // Saldo deposito sama dengan pokoknya, diisi agar konsisten dengan CA dan SA
        dto.setLedgerBalance(principal);
        dto.setAvailableBalance(principal);

        dto.setTenor(p.getTenor());
        dto.setInterestRate(p.getInterestRate() != null ? p.getInterestRate().toPlainString() : null);
        dto.setPlacementDate(p.getPlacementDate() != null ? p.getPlacementDate().format(DATE_FMT) : null);
        dto.setMaturityDate(p.getMaturityDate() != null ? p.getMaturityDate().format(DATE_FMT) : null);

        return dto;
    }

    private ApiResponse<DepositAcctListingDTO> success(DepositAcctListingDTO body) {
        ApiResponse<DepositAcctListingDTO> response = new ApiResponse<>();
        response.setMsgHeader(new ErrorResponse(CODE_SUCCESS));
        response.setMsgBody(body);
        return response;
    }

    private ApiResponse<DepositAcctListingDTO> failure(String message) {
        ErrorResponse header = new ErrorResponse(CODE_FAILED);
        header.setErrorMessage(message);
        ApiResponse<DepositAcctListingDTO> response = new ApiResponse<>();
        response.setMsgHeader(header);
        response.setMsgBody(null);
        return response;
    }
}