package com.m2e.dashboard.service.einvoice;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceStatementDownloadReqDTO;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceStatementListingReqDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMStatementDTO;
import com.m2e.dashboard.dto.einvoice.response.EInvoiceStatementListingDTO;
import com.m2e.dashboard.mapper.einvoice.EInvoiceStatementMapper;
import com.m2e.dashboard.projection.DepositAcctDetailPageProjection;
import com.m2e.dashboard.repository.AccountRepository;
import com.m2e.dashboard.service.einvoice.integration.EIMIntegration;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EInvoiceStatementService {

    private static final Logger log = LoggerFactory.getLogger(EInvoiceStatementService.class);

    private final EInvoiceStatementMapper eInvoiceStatementMapper;
    private final EIMIntegration eimIntegrationService;
    private final AccountRepository accountRepository;

    public ApiResponse<EInvoiceStatementListingDTO> eInvoiceStatementListing(UserAndCorp userAndCorp, EInvoiceStatementListingReqDTO req){
        // override the startDate and Enddate
        LocalDate startDate = LocalDate.parse(req.getStartDate());
        req.setStartDate(startDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        LocalDate endDate = LocalDate.parse(req.getEndDate());
        req.setEndDate(endDate.format(DateTimeFormatter.ofPattern("yyyyMMdd")));

        EIMStatementDTO eimStatement = eimIntegrationService.getListEInvoice(userAndCorp.getUserId(), req);

        ApiResponse<EInvoiceStatementListingDTO> response = new ApiResponse<>();
        response.setMsgHeader(new ErrorResponse());

        EInvoiceStatementListingDTO result = Optional.ofNullable(eimStatement)
                .map(statement -> eInvoiceStatementMapper.toStatementListing(statement, userAndCorp.getCorpId(), req))
                .orElseGet(() -> {
                    log.info("EIM response is null, returning empty result.");
                    return new EInvoiceStatementListingDTO(new SearchCollectionResponse<>());
                });

        response.setMsgBody(result);
        return response;
    }

    public DownloadDTO downloadEInvoiceStatement(EInvoiceStatementDownloadReqDTO req){
        DownloadDTO result = new DownloadDTO();
        Optional<DepositAcctDetailPageProjection> accountData = accountRepository.findAcctDetails(req.getAcctNumber(), req.getCurrency());
        //check if account exists or not
        if (accountData.isEmpty()) {
            log.error("Account Not Found for: " + req.getAcctNumber());
            setEmptyDownload(result);
        }

        byte[] byteData = eimIntegrationService.getDownloadByteData(req.getDownloadUrl());

        result.setData(byteData);
        result.setFileName(req.getFilename());
        return result;
    }

    private DownloadDTO setEmptyDownload(DownloadDTO result){
        result.setData(new byte[0]);
        result.setFileName("");
        return result;
    }
}
