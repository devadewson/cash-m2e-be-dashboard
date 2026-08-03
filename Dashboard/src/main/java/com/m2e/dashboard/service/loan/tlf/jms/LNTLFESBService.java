//package com.m2e.dashboard.service.loan.tlf.jms;
//
//import com.m2e.dashboard.dto.common.ApiResponse;
//import com.m2e.dashboard.dto.loan.tlf.request.LNTLFDetailReqDTO;
//import com.m2e.dashboard.dto.loan.tlf.request.LNTLFTranchesDetailReqDTO;
//import com.m2e.dashboard.dto.loan.tlf.request.LNTrxHistoryReqDTO;
//import com.m2e.dashboard.dto.loan.tlf.response.*;
//import com.m2e.dashboard.dto.loan.tlf.response.acctdetail.*;
//import com.m2e.dashboard.enums.AcctStatusEnum;
//import com.m2e.dashboard.enums.PropertyServiceEnum;
//import com.m2e.dashboard.jms.loan.tlf.request.tranch.details.ESBLoanTLFTranchesReqDTO;
//import com.m2e.dashboard.service.esb.ESBClientService;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import java.util.List;
//
//import static com.m2e.dashboard.enums.ESBServiceType.*;
//
//
//@Service
//public class LNTLFESBService {
//    private static final Logger log = LoggerFactory.getLogger(LNTLFESBService.class);
//
//    private final ESBClientService esbClientService;
//
//    @Autowired
//    public LNTLFESBService(ESBClientService esbClientService) {
//        this.esbClientService = esbClientService;
//    }
//
//    public ApiResponse<LNTLFAcctListingDTO> getAccountBalanceInquiryMYConv(List<LNTLFDTO> request) {
//        log.info("getAccountBalanceInquiryConv: {}", request);
//        List<LNTLFDTO> activeAccounts = request.stream().filter(acct-> !AcctStatusEnum.CLOSED.name().equalsIgnoreCase(acct.getAcctStatusDesc())).toList();
//        return esbClientService.execute(activeAccounts, LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_CONV, LNTLFAcctListingDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFAcctListingDTO> getAccountBalanceInquiryMYIslamic(List<LNTLFDTO> request) {
//        log.info("getAccountBalanceInquiryIslamic: {}", request);
//        List<LNTLFDTO> activeAccounts = request.stream().filter(acct-> !AcctStatusEnum.CLOSED.name().equalsIgnoreCase(acct.getAcctStatusDesc())).toList();
//        return esbClientService.execute(activeAccounts, LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_ISLAMIC, LNTLFAcctListingDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFAcctListingDTO> getAccountBalanceInquiryMYForeignConv(List<LNTLFDTO> request) {
//        log.info("getAccountBalanceInquiryForeignConv: {}", request);
//        List<LNTLFDTO> activeAccounts = request.stream().filter(acct-> !AcctStatusEnum.CLOSED.name().equalsIgnoreCase(acct.getAcctStatusDesc())).toList();
//        return esbClientService.execute(activeAccounts, LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_FOREIGN_CONV, LNTLFAcctListingDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFAcctListingDTO> getAccountBalanceInquiryMYForeignIslamic(List<LNTLFDTO> request) {
//        log.info("getAccountBalanceInquiryForeignIslamic: {}", request);
//        List<LNTLFDTO> activeAccounts = request.stream().filter(acct-> !AcctStatusEnum.CLOSED.name().equalsIgnoreCase(acct.getAcctStatusDesc())).toList();
//        return esbClientService.execute(activeAccounts, LOAN_TLF_ACCT_BALANCE_INQUIRY_MY_FOREIGN_ISLAMIC, LNTLFAcctListingDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFTrxHistoryResponseDTO> getLoanTLFTrxHistoryMYConv(LNTrxHistoryReqDTO request) {
//        log.info("getLoanTLFTrxHistoryMYConv: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_ACCT_ACTIVITY_INQUIRY_MY_CONV, LNTLFTrxHistoryResponseDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFTrxHistoryResponseDTO> getLoanTLFTrxHistoryMYIslamic(LNTrxHistoryReqDTO request) {
//        log.info("getLoanTLFTrxHistoryMYIslamic: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_ACCT_ACTIVITY_INQUIRY_MY_ISLAMIC, LNTLFTrxHistoryResponseDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFTrxHistoryResponseDTO> getLoanTLFTrxHistoryMYForeignIslamic(LNTrxHistoryReqDTO request) {
//        log.info("getLoanTLFTrxHistoryMYForeignIslamic: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_ACCT_ACTIVITY_INQUIRY_MY_FOREIGN_ISLAMIC, LNTLFTrxHistoryResponseDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFTrxHistoryResponseDTO> getLoanTLFTrxHistoryMYForeignConv(LNTrxHistoryReqDTO request) {
//        log.info("getLoanTLFTrxHistoryMYForeignConv: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_ACCT_ACTIVITY_INQUIRY_MY_FOREIGN_CONV, LNTLFTrxHistoryResponseDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFDetailConventionalDTO> getLoanTLFAcctLoanDetailsMYConv(LNTLFDetailReqDTO request) {
//        log.info("getLoanTLFAcctLoanDetailsMYConv: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_ACCT_DETAILS_MY_CONV, LNTLFDetailConventionalDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFDetailIslamicDTO> getLoanTLFAcctLoanDetailsMYIslamic(LNTLFDetailReqDTO request) {
//        log.info("getLoanTLFAcctLoanDetailsMYIslamic: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_ACCT_DETAILS_MY_ISLAMIC, LNTLFDetailIslamicDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFDetailMYForeignConventionalDTO> getLoanTLFAcctFinancingDetailsMYConv(LNTLFDetailReqDTO request) {
//        log.info("getLoanTLFAcctFinancingDetailsMYConv: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_ACCT_DETAILS_MY_FOREIGN_CONV, LNTLFDetailMYForeignConventionalDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFDetailMYForeignIslamicDTO> getLoanTLFAcctFinancingDetailsMYIslamic(LNTLFDetailReqDTO request) {
//        log.info("getLoanTLFAcctFinancingDetailsMYIslamic: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_ACCT_DETAILS_MY_FOREIGN_ISLAMIC, LNTLFDetailMYForeignIslamicDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFDetailSGSIBSDTO> getLoanTLFAcctLoanDetailsSGConv(LNTLFDetailReqDTO request) {
//        log.info("getLoanTLFAcctLoanDetailsSGConv: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_ACCT_DETAILS_SG_SIBS_CONV, LNTLFDetailSGSIBSDTO::new, PropertyServiceEnum.ESBSG);
//    }
//
//    public ApiResponse<LNTLFDetailSGSIBSDTO> getLoanTLFAcctLoanDetailsSGIslamic(LNTLFDetailReqDTO request) {
//        log.info("getLoanTLFAcctLoanDetailsSGIslamic: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_ACCT_DETAILS_SG_SIBS_ISLAMIC, LNTLFDetailSGSIBSDTO::new, PropertyServiceEnum.ESBSG);
//    }
//
//    public ApiResponse<LNTLFAcctListingDTO> getAccountBalanceInquirySGSIBSConv(List<LNTLFDTO> request) {
//        log.info("getLoanTLFAcctFinancingDetailsSGSIBSConv: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_ACCT_BALANCE_INQUIRY_SG_SIBS_CONV, LNTLFAcctListingDTO::new, PropertyServiceEnum.ESBSG);
//    }
//
//    public ApiResponse<LNTLFAcctListingDTO> getAccountBalanceInquirySGSIBSIslamic(List<LNTLFDTO> request) {
//        log.info("getLoanTLFAcctFinancingDetailsSGSIBSIslamic: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_ACCT_BALANCE_INQUIRY_SG_SIBS_ISLAMIC, LNTLFAcctListingDTO::new, PropertyServiceEnum.ESBSG);
//    }
//
//    public ApiResponse<LNTLFTrancheListDTO> getLoanTLFTranchesConv(ESBLoanTLFTranchesReqDTO request) {
//        log.info("getLoanTLFTranchesConv: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_TRANCHES_INQUIRY_FOREIGN_CONV, LNTLFTrancheListDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFTrancheListDTO> getLoanTLFTranchesIslamic(ESBLoanTLFTranchesReqDTO request) {
//        log.info("getLoanTLFTranchesIslamic: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_TRANCHES_INQUIRY_FOREIGN_ISLAMIC, LNTLFTrancheListDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFTranchesDetailConventionalDTO> getLoanTLFTrancheConvDetail(LNTLFTranchesDetailReqDTO request) {
//        log.info("getLoanTLFTrancheConvDetail: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_TRANCH_DETAIL_INQUIRY_CONV, LNTLFTranchesDetailConventionalDTO::new, PropertyServiceEnum.ESBMY);
//    }
//
//    public ApiResponse<LNTLFTranchesDetailIslamicDTO> getLoanTLFTrancheIslamicDetail(LNTLFTranchesDetailReqDTO request) {
//        log.info("getLoanTLFTrancheIslamicDetail: {}", request);
//        return esbClientService.execute(request, LOAN_TLF_TRANCH_DETAIL_INQUIRY_FOREIGN_ISLAMIC, LNTLFTranchesDetailIslamicDTO::new, PropertyServiceEnum.ESBMY);
//    }
//}
//
