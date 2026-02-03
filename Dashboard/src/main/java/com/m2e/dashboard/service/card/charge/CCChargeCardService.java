package com.m2e.dashboard.service.card.charge;

import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.card.charge.request.*;
import com.m2e.dashboard.dto.card.charge.response.CCChargeCardDetailDTO;
import com.m2e.dashboard.dto.card.charge.response.CCChargeCardListDTO;
import com.m2e.dashboard.dto.card.common.request.CCDetailReqDTO;
import com.m2e.dashboard.dto.card.common.request.CCPaymentReqDTO;
import com.m2e.dashboard.dto.card.common.request.CCStatsReqDTO;
import com.m2e.dashboard.dto.card.common.response.CCStatsDTO;
import com.m2e.dashboard.dto.card.common.response.CCTrxHistoryDTO;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.DownloadEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CCChargeCardService {
    @Autowired
    private CCChargeCardDataGenerator dataGenerator;
    @Autowired
    private CCChargeCardAccListFileService accListFileService;
    @Autowired
    private CCChargeCardTrxHistFileService trxHistFileService;
    @Autowired
    private CCChargeCardPendTrxHistFileService pendTrxHistFileService;

    public ApiResponse<CCStatsDTO> getAccountStats(String auth, CCChargeCardStatsReqDTO req) {
        // TODO : replace data generator with the real business logic

        if (req.isTodayToggle()) {
            return new ApiResponse<>(new ErrorResponse(), CCStatsDTO.builder()
                    .lastUpdatedTime(LocalDateTime.now().toString())
                    .numberOfPrincipalCard("40")
                    .build());
        }
        return new ApiResponse<>(new ErrorResponse(), CCStatsDTO.builder()
                .lastUpdatedTime(LocalDateTime.now().toString())
                .numberOfPrincipalCard("40")
                .creditLimit(dataGenerator.consolidatedValue().toString())
                .outstandingBalance(dataGenerator.consolidatedValue().toString())
                .currency("MYR")
                .build());

    }

    public ApiResponse<SearchCollectionResponse<CCChargeCardListDTO>> getAccountListing(String auth, CCChargeCardAccListReqDTO req) {
        // TODO : replace data generator with the real business logic
        Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize());
        List<CCChargeCardListDTO> list = dataGenerator.chargeCardListing("MYR");

        Page<CCChargeCardListDTO> page = new PageImpl<>(list, pageable, list.size());

        SearchCollectionResponse<CCChargeCardListDTO> accountListResp = new SearchCollectionResponse<>();
        accountListResp.setCurrentPage(req.getPage());
        accountListResp.setNextPage(page.hasNext() ? req.getPage() + 1 : -1);
        accountListResp.setTotalPages(page.getTotalPages());
        accountListResp.setTotalRecordCount(list.size());
        accountListResp.setData(page.getContent());

        return new ApiResponse<>(new ErrorResponse(), accountListResp);
    }

    public ApiResponse<CCChargeCardDetailDTO> getAccountDetail(CCChargeCardDetailReqDTO req) {
        // TODO : replace data generator with the real business logic

        CCChargeCardDetailDTO data = dataGenerator.getAccountDetail(req.getCardNumber(), "MYR", req.getCardName());

        return new ApiResponse(new ErrorResponse(), data);
    }

    public ApiResponse<SearchCollectionResponse<CCTrxHistoryDTO>> retrieveTrxHistory(CCChargeCardTrxHistReqDTO req) {
        // TODO : replace data generator with the real business logic
        Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize());

        List<CCTrxHistoryDTO> list = dataGenerator.retrieveTrxHistory("MYR");
        Page<CCTrxHistoryDTO> page = new PageImpl<>(list, pageable, list.size());

        return new ApiResponse<>(new ErrorResponse(), new SearchCollectionResponse<>(pageable.getPageNumber(), page));
    }

    public ApiResponse<SearchCollectionResponse<CCTrxHistoryDTO>> retrievePendingTrxHistory(CCChargeCardTrxHistReqDTO req) {
        // TODO : replace data generator with the real business logic
        Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize());

        List<CCTrxHistoryDTO> list = dataGenerator.retrievePendingTrxHistory("MYR");
        Page<CCTrxHistoryDTO> page = new PageImpl<>(list, pageable, list.size());

        return new ApiResponse<>(new ErrorResponse(), new SearchCollectionResponse<>(pageable.getPageNumber(), page));
    }

    public ApiResponse<?> makePayment(String auth, CCPaymentReqDTO req) {
        // TODO : replace with the real business logic
        return new ApiResponse<>();
    }

    public DownloadDTO downloadCC(String auth, DownloadReq req) {
        DownloadDTO result = new DownloadDTO();
        switch (req.getReportType()) {
            case CHARGE_CARD_LISTING_RPT_TYPE -> {
                CCChargeCardListDLReqDTO reqData = (CCChargeCardListDLReqDTO) req;
                downloadAcctSummary(result, reqData);
            }
            case CHARGE_CARD_TRXHISTORIES_RPT_TYPE -> {
                CCChargeCardTrxHistDLReqDTO reqData = (CCChargeCardTrxHistDLReqDTO) req;
                downloadTrxHistory(result, reqData);
            }
            case CHARGE_CARD_PENDING_TRX_RPT_TYPE -> {
                CCChargeCardPendHistDLReqDTO reqData = (CCChargeCardPendHistDLReqDTO) req;
                downloadPendingTrx(result, reqData);
            }
            default -> {
                throw new IllegalArgumentException("Report type is undefined");
            }
        }
        return result;
    }

    public DownloadDTO downloadAcctSummary(DownloadDTO result, CCChargeCardListDLReqDTO req) {
        // TODO : replace data generator with the real business logic
        List<CCChargeCardListDTO> data = dataGenerator.chargeCardListing("MYR");
        switch (req.getFormat()) {
            case CSV:
                result.setData(accListFileService.generateCsv(data));
                result.setFileName(DownloadEnum.CHARGE_CARD_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                result.setData(accListFileService.generatePdf(data));
                result.setFileName(DownloadEnum.CHARGE_CARD_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                result.setData(accListFileService.generateTxt(data));
                result.setFileName(DownloadEnum.CHARGE_CARD_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    public DownloadDTO downloadTrxHistory(DownloadDTO result, CCChargeCardTrxHistDLReqDTO req) {
        // TODO : replace data generator with the real business logic
        List<CCTrxHistoryDTO> data = dataGenerator.retrieveTrxHistory("MYR");
        switch (req.getFormat()) {
            case CSV:
                result.setData(trxHistFileService.generateCsv(data));
                result.setFileName(DownloadEnum.CHARGE_CARD_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                result.setData(trxHistFileService.generatePdf(data));
                result.setFileName(DownloadEnum.CHARGE_CARD_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                result.setData(trxHistFileService.generateTxt(data));
                result.setFileName(DownloadEnum.CHARGE_CARD_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    public DownloadDTO downloadPendingTrx(DownloadDTO result, CCChargeCardPendHistDLReqDTO req) {
        // TODO : replace data generator with the real business logic
        List<CCTrxHistoryDTO> data = dataGenerator.retrievePendingTrxHistory("MYR");
        switch (req.getFormat()) {
            case CSV:
                result.setData(pendTrxHistFileService.generateCsv(data));
                result.setFileName(DownloadEnum.CHARGE_CARD_PENDING_TRX_FILENAME.getDescription() + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                result.setData(pendTrxHistFileService.generatePdf(data));
                result.setFileName(DownloadEnum.CHARGE_CARD_PENDING_TRX_FILENAME.getDescription() + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                result.setData(pendTrxHistFileService.generateTxt(data));
                result.setFileName(DownloadEnum.CHARGE_CARD_PENDING_TRX_FILENAME.getDescription() + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }
}
