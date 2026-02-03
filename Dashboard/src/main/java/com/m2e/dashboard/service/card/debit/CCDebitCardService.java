package com.m2e.dashboard.service.card.debit;

import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.card.common.request.CCStatsReqDTO;
import com.m2e.dashboard.dto.card.debit.request.CCDebitCardAccListReqDTO;
import com.m2e.dashboard.dto.card.debit.request.CCDebitCardListDLReqDTO;
import com.m2e.dashboard.dto.card.debit.response.CCDebitAccListBaseDTO;
import com.m2e.dashboard.dto.card.debit.response.CCDebitCardListMYDTO;
import com.m2e.dashboard.dto.card.debit.response.CCDebitCardListSGDTO;
import com.m2e.dashboard.dto.card.debit.response.CCDebitCardStatsDTO;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.DownloadEnum;
import com.m2e.dashboard.enums.ProviderEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CCDebitCardService {
    @Autowired
    private CCDebitCardDataGenerator dataGenerator;
    @Autowired
    private CCDebitCardAccListMYFileService accListFileService;

    @Autowired
    private CCDebitCardAccListSGFileService accListSGFileService;

    public ApiResponse<CCDebitCardStatsDTO> getAccountStats(String auth, CCStatsReqDTO req) {
        // TODO : replace data generator with the real business logic

        return new ApiResponse<>(new ErrorResponse(), CCDebitCardStatsDTO.builder()
                .lastUpdatedTime(LocalDateTime.now().toString())
                .numberOfCard("50")
                .build());

    }

    public <T extends CCDebitAccListBaseDTO> ApiResponse<SearchCollectionResponse<T>> getAccountListing(String auth, CCDebitCardAccListReqDTO req) {
        // TODO : replace data generator with the real business logic
        Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize());
        List<T> list = null;

        if (req.getProvider() == ProviderEnum.MY) {
            list = (List<T>) dataGenerator.debitCardMYListing("MYR");
        } else {
            list = (List<T>) dataGenerator.debitCardSGListing("SGD");
        }

        Page<T> page = new PageImpl<>(list, pageable, list.size());

        SearchCollectionResponse<T> accountListResp = new SearchCollectionResponse<>();
        accountListResp.setCurrentPage(req.getPage());
        accountListResp.setNextPage(page.hasNext() ? req.getPage() + 1 : -1);
        accountListResp.setTotalPages(page.getTotalPages());
        accountListResp.setTotalRecordCount(list.size());
        accountListResp.setData(page.getContent());

        return new ApiResponse<>(new ErrorResponse(), accountListResp);
    }

    public DownloadDTO downloadCC(String auth, CCDebitCardListDLReqDTO req) {
        DownloadDTO result = new DownloadDTO();

        if (req.getProvider().equals(ProviderEnum.MY))
            downloadMYAcctSummary(result, req);
        else
            downloadSGAcctSummary(result, req);

        return result;
    }

    public DownloadDTO downloadMYAcctSummary(DownloadDTO result, CCDebitCardListDLReqDTO req) {
        // TODO : replace data generator with the real business logic
        List<CCDebitCardListMYDTO> data = dataGenerator.debitCardMYListing("MYR");
        switch (req.getFormat()) {
            case CSV:
                result.setData(accListFileService.generateCsv(data));
                result.setFileName(DownloadEnum.DEBIT_CARD_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                result.setData(accListFileService.generatePdf(data));
                result.setFileName(DownloadEnum.DEBIT_CARD_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                result.setData(accListFileService.generateTxt(data));
                result.setFileName(DownloadEnum.DEBIT_CARD_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    public DownloadDTO downloadSGAcctSummary(DownloadDTO result, CCDebitCardListDLReqDTO req) {
        // TODO : replace data generator with the real business logic
        List<CCDebitCardListSGDTO> data = dataGenerator.debitCardSGListing("SGD");
        switch (req.getFormat()) {
            case CSV:
                result.setData(accListSGFileService.generateCsv(data));
                result.setFileName(DownloadEnum.DEBIT_CARD_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                result.setData(accListSGFileService.generatePdf(data));
                result.setFileName(DownloadEnum.DEBIT_CARD_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                result.setData(accListSGFileService.generateTxt(data));
                result.setFileName(DownloadEnum.DEBIT_CARD_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }
}
