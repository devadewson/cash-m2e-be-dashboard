package com.m2e.dashboard.service.card.credit;

import com.m2e.dashboard.auth.JwtUtil;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.card.common.request.CCDetailReqDTO;
import com.m2e.dashboard.dto.card.common.request.CCPaymentReqDTO;
import com.m2e.dashboard.dto.card.common.request.CCStatsReqDTO;
import com.m2e.dashboard.dto.card.common.response.CCAccListBaseDTO;
import com.m2e.dashboard.dto.card.common.response.CCBaseCardDetailDTO;
import com.m2e.dashboard.dto.card.common.response.CCStatsDTO;
import com.m2e.dashboard.dto.card.common.response.CCTrxHistoryDTO;
import com.m2e.dashboard.dto.card.credit.request.*;
import com.m2e.dashboard.dto.card.credit.response.CCCreditCardListMYDTO;
import com.m2e.dashboard.dto.card.credit.response.CCCreditCardMasterDetailSGDTO;
import com.m2e.dashboard.dto.card.credit.response.CCCreditCardMasterListSGDTO;
import com.m2e.dashboard.dto.card.credit.response.CCCreditCardMemberDTO;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.CreditCardType;
import com.m2e.dashboard.enums.DownloadEnum;
import com.m2e.dashboard.enums.ProviderEnum;
import com.m2e.dashboard.repository.CorpUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CCCreditCardService {
    @Autowired
    private CCCreditCardDataGenerator dataGenerator;
    @Autowired
    private CCCreditCardAccListFileService accListFileService;
    @Autowired
    private CCCreditCardTrxHistFileService trxHistFileService;
    @Autowired
    private CCCreditCardPendTrxHistFileService pendTrxHistFileService;
    @Autowired
    private CCCreditCardMasterAcctListFileService masterAcctListFileService;

    public ApiResponse<CCStatsDTO> getAccountStats(String auth, CCStatsReqDTO req) {
        // TODO : replace data generator with the real business logic

        if (req.getProvider() == ProviderEnum.MY) {
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
        } else {
            if (req.isTodayToggle()) {
                return new ApiResponse<>(new ErrorResponse(), CCStatsDTO.builder()
                        .lastUpdatedTime(LocalDateTime.now().toString())
                        .build());
            }
            return new ApiResponse<>(new ErrorResponse(), CCStatsDTO.builder()
                    .lastUpdatedTime(LocalDateTime.now().toString())
                    .creditLimit(dataGenerator.consolidatedValue().toString())
                    .outstandingBalance(dataGenerator.consolidatedValue().toString())
                    .currency("SGD")
                    .build());
        }
    }

    public ApiResponse<CCStatsDTO> getMasterAccountStats(String auth, CCStatsReqDTO req) {
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
                .currency("SGD")
                .build());
    }

    public <T extends CCAccListBaseDTO> ApiResponse<SearchCollectionResponse<T>> getAccountListing(String auth, CCCreditCardAccListReqDTO req) {
        // TODO : replace data generator with the real business logic
        Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize());
        List<T> list = null;
        if (req.getProvider() == ProviderEnum.MY) {
            if (req.getCardType().contains(CreditCardType.ALL_CARD)) {
                list = (List<T>) dataGenerator.creditCardListing("MYR");
            } else {
                list = (List<T>) dataGenerator.creditCardListing("SGD").stream()
                        .filter(data -> req.getCardType().stream()
                                .anyMatch(type -> data.getCardName().toLowerCase().contains(type.getDescription().toLowerCase())))
                        .collect(Collectors.toList());
            }
        } else {
            list = (List<T>) dataGenerator.getAllSGListing("SGD");
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

    public ApiResponse<SearchCollectionResponse<CCCreditCardMasterListSGDTO>> getMasterAccountListing(String auth, CCCreditCardMasterListReqDTO req) {
        // TODO : replace data generator with the real business logic
        Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize());
        List<CCCreditCardMasterListSGDTO> list = dataGenerator.getMasterListing("SGD");

        Page<CCCreditCardMasterListSGDTO> page = new PageImpl<>(list, pageable, list.size());

        SearchCollectionResponse<CCCreditCardMasterListSGDTO> accountListResp = new SearchCollectionResponse<>();
        accountListResp.setCurrentPage(req.getPage());
        accountListResp.setNextPage(page.hasNext() ? req.getPage() + 1 : -1);
        accountListResp.setTotalPages(page.getTotalPages());
        accountListResp.setTotalRecordCount(list.size());
        accountListResp.setData(page.getContent());

        return new ApiResponse<>(new ErrorResponse(), accountListResp);
    }

    public ApiResponse<CCBaseCardDetailDTO> getAccountDetail(CCDetailReqDTO req) {
        // TODO : replace data generator with the real business logic
        Object data = null;
        switch (req.getProvider()) {
            case MY ->
                    data = dataGenerator.getAccountDetailMY(req.getCardNumber(), "MYR", req.getCardName());
            case SG ->
                    data = dataGenerator.getAccountDetailSG(req.getCardNumber(), "SGD", req.getCardName());
        }
        return new ApiResponse(new ErrorResponse(), data);
    }

    public ApiResponse<CCCreditCardMasterDetailSGDTO> getMasterAccountDetail(CCDetailReqDTO req) {
        // TODO : replace data generator with the real business logic
        CCCreditCardMasterDetailSGDTO data = dataGenerator.getMasterAccountDetail(req.getCardNumber(), "SGD", req.getCardName());
        return new ApiResponse<>(new ErrorResponse(), data);
    }

    public ApiResponse<SearchCollectionResponse<CCTrxHistoryDTO>> retrieveTrxHistory(CCCreditCardTrxHistReqDTO req) {
        // TODO : replace data generator with the real business logic
        Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize());

        List<CCTrxHistoryDTO> list = dataGenerator.retrieveTrxHistory("MYR");
        Page<CCTrxHistoryDTO> page = new PageImpl<>(list, pageable, list.size());

        return new ApiResponse<>(new ErrorResponse(), new SearchCollectionResponse<>(pageable.getPageNumber(), page));
    }

    public ApiResponse<SearchCollectionResponse<CCTrxHistoryDTO>> retrievePendingTrxHistory(CCCreditCardTrxHistReqDTO req) {
        // TODO : replace data generator with the real business logic
        Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize());

        List<CCTrxHistoryDTO> list = dataGenerator.retrievePendingTrxHistory("MYR");
        Page<CCTrxHistoryDTO> page = new PageImpl<>(list, pageable, list.size());

        return new ApiResponse<>(new ErrorResponse(), new SearchCollectionResponse<>(pageable.getPageNumber(), page));
    }

    public ApiResponse<SearchCollectionResponse<CCCreditCardMemberDTO>> retrieveCardMemberList(CCCreditCardMemberReqDTO req) {
        // TODO : replace data generator with the real business logic
        Pageable pageable = PageRequest.of(req.getPage(), req.getPageSize());
        List<CCCreditCardMemberDTO> list = new ArrayList<>();

        if(req.getProvider().equals(ProviderEnum.MY))
            list = dataGenerator.retrieveCardMember();
        else
            list = dataGenerator.retrieveCardMemberSG();

        Page<CCCreditCardMemberDTO> page = new PageImpl<>(list, pageable, list.size());

        return new ApiResponse<>(new ErrorResponse(), new SearchCollectionResponse<>(pageable.getPageNumber(), page));
    }

    public ApiResponse<List<CCCreditCardMemberDTO>> getCardMemberDropDownList(CCCreditCardMemberDropDownReqDTO request) {
        // TODO : replace data generator with the real business logic
        List<CCCreditCardMemberDTO> list = dataGenerator.retrieveCardMember();

        return new ApiResponse<>(new ErrorResponse(), list);
    }

    public ApiResponse<?> makePayment(String auth, CCPaymentReqDTO req) {
        // TODO : replace with the real business logic
        return new ApiResponse<>();
    }

    public DownloadDTO downloadCC(String auth, DownloadReq req) {
        DownloadDTO result = new DownloadDTO();
        switch (req.getReportType()) {
            case CREDIT_CARD_LISTING_RPT_TYPE -> {
                CCCreditCardListDLReqDTO reqData = (CCCreditCardListDLReqDTO) req;
                downloadAcctSummary(result, reqData);
            }
            case CREDIT_CARD_TRXHISTORIES_RPT_TYPE -> {
                CCCreditCardTrxHistDLReqDTO reqData = (CCCreditCardTrxHistDLReqDTO) req;
                downloadTrxHistory(result, reqData);
            }
            case CREDIT_CARD_PENDING_TRX_RPT_TYPE -> {
                CCCreditCardPendHistDLReqDTO reqData = (CCCreditCardPendHistDLReqDTO) req;
                downloadPendingTrx(result, reqData);
            }
            case CREDIT_CARD_MASTER_LISTING_RPT_TYPE -> {
                CCCreditCardMasterListDLReqDTO reqData = (CCCreditCardMasterListDLReqDTO) req;
                downloadMasterList(result, reqData);
            }
            default -> {
                throw new IllegalArgumentException("Report type is undefined");
            }
        }
        return result;
    }

    public DownloadDTO downloadAcctSummary(DownloadDTO result, CCCreditCardListDLReqDTO req) {
        // TODO : replace data generator with the real business logic
        List<CCCreditCardListMYDTO> data = dataGenerator.creditCardListing("MYR");
        switch (req.getFormat()) {
            case CSV:
                result.setData(accListFileService.generateCsv(data));
                result.setFileName(DownloadEnum.CREDIT_CARD_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                result.setData(accListFileService.generatePdf(data));
                result.setFileName(DownloadEnum.CREDIT_CARD_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                result.setData(accListFileService.generateTxt(data));
                result.setFileName(DownloadEnum.CREDIT_CARD_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    public DownloadDTO downloadTrxHistory(DownloadDTO result, CCCreditCardTrxHistDLReqDTO req) {
        // TODO : replace data generator with the real business logic
        List<CCTrxHistoryDTO> data = dataGenerator.retrieveTrxHistory("MYR");
        switch (req.getFormat()) {
            case CSV:
                result.setData(trxHistFileService.generateCsv(data));
                result.setFileName(DownloadEnum.CREDIT_CARD_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                result.setData(trxHistFileService.generatePdf(data));
                result.setFileName(DownloadEnum.CREDIT_CARD_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                result.setData(trxHistFileService.generateTxt(data));
                result.setFileName(DownloadEnum.CREDIT_CARD_TRXHISTORIES_FILENAME.getDescription() + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    public DownloadDTO downloadPendingTrx(DownloadDTO result, CCCreditCardPendHistDLReqDTO req) {
        // TODO : replace data generator with the real business logic
        List<CCTrxHistoryDTO> data = dataGenerator.retrievePendingTrxHistory("MYR");
        switch (req.getFormat()) {
            case CSV:
                result.setData(pendTrxHistFileService.generateCsv(data));
                result.setFileName(DownloadEnum.CREDIT_CARD_PENDING_TRX_FILENAME.getDescription() + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                result.setData(pendTrxHistFileService.generatePdf(data));
                result.setFileName(DownloadEnum.CREDIT_CARD_PENDING_TRX_FILENAME.getDescription() + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                result.setData(pendTrxHistFileService.generateTxt(data));
                result.setFileName(DownloadEnum.CREDIT_CARD_PENDING_TRX_FILENAME.getDescription() + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }

    public DownloadDTO downloadMasterList(DownloadDTO result, CCCreditCardMasterListDLReqDTO req) {
        // TODO : replace data generator with the real business logic
        List<CCCreditCardMasterListSGDTO> data = dataGenerator.getMasterListing("SGD");
        switch (req.getFormat()) {
            case CSV:
                result.setData(masterAcctListFileService.generateCsv(data));
                result.setFileName(DownloadEnum.CREDIT_CARD_MASTER_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".csv");
                break;
            case PDF:
                result.setData(masterAcctListFileService.generatePdf(data));
                result.setFileName(DownloadEnum.CREDIT_CARD_MASTER_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".pdf");
                break;
            case TXT:
                result.setData(masterAcctListFileService.generateTxt(data));
                result.setFileName(DownloadEnum.CREDIT_CARD_MASTER_ACCLISTING_FILENAME.getDescription() + System.currentTimeMillis() + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
        return result;
    }
}
