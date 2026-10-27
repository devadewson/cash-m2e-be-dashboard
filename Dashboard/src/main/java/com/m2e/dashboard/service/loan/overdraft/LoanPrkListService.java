package com.m2e.dashboard.service.loan.overdraft;

import com.m2e.dashboard.config.CorpCifResolver;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.loan.LNAcctListReq;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.loan.overdraft.response.LNOverdraftAcctListingDTO;
import com.m2e.dashboard.dto.loan.overdraft.response.LNOverdraftDTO;
import com.m2e.dashboard.projection.LoanPrkAwalTotalsProjection;
import com.m2e.dashboard.projection.LoanPrkListingProjection;
import com.m2e.dashboard.repository.LoanPrkRepository;
import com.m2e.dashboard.service.overview.Datamart.DatamartDateRange;
import com.m2e.dashboard.service.overview.Datamart.DatamartDateResolver;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LoanPrkListService {

    private final LoanPrkRepository repository;
    private final CorpCifResolver corpCifResolver;
    private final DatamartDateResolver dateResolver;

    private static final String CODE_SUCCESS = "0";
    private static final String CODE_FAILED = "1";
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String ZERO_AMOUNT = "0.00";
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Cacheable(
            value = "loanPrkListing",
            key = "#user.corpId + '_' + #req.page + '_' + #req.pageSize + '_' "
                    + "+ T(java.time.LocalDate).now()",
            unless = "#result == null || #result.msgBody == null"
    )
    public ApiResponse<LNOverdraftAcctListingDTO> getAccountList(UserAndCorp user, LNAcctListReq req) {

        String cifNo;
        try {
            cifNo = corpCifResolver.resolveCif(user.getCorpId());
        } catch (Exception e) {
            log.error("[PRK-List] CIF gagal di-resolve corpId={}: {}", user.getCorpId(), e.getMessage());
            return failure("CIF tidak ditemukan untuk korporasi ini");
        }

        try {
            // FE mengirim page mulai 0, sesuai PageRequest
            Pageable pageable = toPageable(req.getPage(), req.getPageSize());

            Page<LNOverdraftDTO> mappedPage;
            String plafond = ZERO_AMOUNT;
            String limitAwal = ZERO_AMOUNT;

            Optional<DatamartDateRange> range = dateResolver.prk();

            if (range.isEmpty()) {
                log.warn("[PRK-List] Belum ada data PRK di database");
                mappedPage = Page.empty(pageable);
            } else {
                DatamartDateRange r = range.get();

                log.info("[PRK-List] START cif={} page={} pageSize={} dataDate={}",
                        cifNo, pageable.getPageNumber(), pageable.getPageSize(), r.dataDate());

                Page<LoanPrkListingProjection> resultPage =
                        repository.getListing(cifNo, r.startOfDay(), r.endOfDay(), pageable);
                mappedPage = resultPage.map(this::toDto);

                // Total dihitung dari seluruh rekening CIF pada tanggal muat yang sama dengan listing
                LoanPrkAwalTotalsProjection totals =
                        repository.sumAwalTotals(cifNo, r.startOfDay(), r.endOfDay());
                if (totals != null) {
                    plafond = toAmount(totals.getTotalAvailableLimitAwal());
                    limitAwal = toAmount(totals.getTotalLimitAwal());
                }
            }

            SearchCollectionResponse<LNOverdraftDTO> result = new SearchCollectionResponse<>(mappedPage);

            LNOverdraftAcctListingDTO dto = new LNOverdraftAcctListingDTO();
            dto.setCorpCifNo(cifNo);
            dto.setPlafond(plafond);
            dto.setLimitAwal(limitAwal);
            dto.setDatas(result);

            log.info("[PRK-List] SUCCESS cif={} page={} totalRecords={} plafond={} limitAwal={}",
                    cifNo, pageable.getPageNumber(), result.getTotalRecordCount(), plafond, limitAwal);

            return success(dto);

        } catch (Exception e) {
            log.error("[PRK-List] FAILED cif={}: {}", cifNo, e.getMessage(), e);
            return failure("Data PRK tidak dapat diproses saat ini");
        }
    }

    private Pageable toPageable(Integer page, Integer pageSize) {
        int p = (page == null || page < 0) ? 0 : page;
        int size = (pageSize == null || pageSize <= 0) ? DEFAULT_PAGE_SIZE : pageSize;
        return PageRequest.of(p, size);
    }

    private LNOverdraftDTO toDto(LoanPrkListingProjection p) {
        LNOverdraftDTO dto = new LNOverdraftDTO();
        dto.setAccount(p.getAccountNo());
        dto.setCurrency(p.getCurrency());
        dto.setLimit(toAmount(p.getLimitAmount()));
        dto.setOutstanding(toAmount(p.getOutstanding()));
        dto.setMaturity(p.getMaturityDate() != null ? p.getMaturityDate().format(DATE_FMT) : null);
        dto.setInterestDue(toAmount(p.getInterestDue()));
        return dto;
    }

    private static String toAmount(BigDecimal value) {
        return value == null
                ? ZERO_AMOUNT
                : value.setScale(2, RoundingMode.HALF_UP).toPlainString();
    }

    private ApiResponse<LNOverdraftAcctListingDTO> success(LNOverdraftAcctListingDTO body) {
        ApiResponse<LNOverdraftAcctListingDTO> response = new ApiResponse<>();
        response.setMsgHeader(new ErrorResponse(CODE_SUCCESS));
        response.setMsgBody(body);
        return response;
    }

    private ApiResponse<LNOverdraftAcctListingDTO> failure(String message) {
        ErrorResponse header = new ErrorResponse(CODE_FAILED);
        header.setErrorMessage(message);
        ApiResponse<LNOverdraftAcctListingDTO> response = new ApiResponse<>();
        response.setMsgHeader(header);
        response.setMsgBody(null);
        return response;
    }
}