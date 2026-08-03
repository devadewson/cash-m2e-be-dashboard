package com.m2e.dashboard.service.deposit.ca.Rest;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.deposit.ca.request.DepositAccountDetailReqDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAccountDetailDTO;
import com.m2e.dashboard.model.AccountInquiry.Response.MsgWraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepositCAAccountDetailService {
    @Qualifier("btsWebClient")
    private final ESBAccountInquiryClient esbAccountInquiryClient;

    public ApiResponse<DepositAccountDetailDTO> getAccountDetail(
            UserAndCorp user,
            DepositAccountDetailReqDTO req) {

        log.info(
                "START Account Detail Inquiry | acctNo={} | currency={} | corpCif={}",
                req.getAcctNumber(),
                req.getCurrency(),
                req.getCorpCifNo()
        );

        try {

            var esbRequest = buildESBRequest(user, req);

            log.info(
                    "Calling ESB Account Detail | acctNo={} | currency={}",
                    req.getAcctNumber(),
                    req.getCurrency()
            );

            MsgWraper esbResponse =
                    esbAccountInquiryClient.getAccountDetail(esbRequest);

            log.info(
                    "ESB Account Detail Response received successfully | acctNo={}",
                    req.getAcctNumber()
            );

            DepositAccountDetailDTO dto =
                    mapEsbToDepositAccountDetail(esbResponse);

            ApiResponse<DepositAccountDetailDTO> response =
                    new ApiResponse<>();

            response.setMsgHeader(new ErrorResponse("0"));
            response.setMsgBody(dto);

            log.info(
                    "END Account Detail Inquiry SUCCESS | acctNo={}",
                    req.getAcctNumber()
            );

            return response;

        } catch (Exception ex) {

            log.error(
                    "Account Detail Inquiry FAILED | acctNo={}",
                    req.getAcctNumber(),
                    ex
            );

            ApiResponse<DepositAccountDetailDTO> response =
                    new ApiResponse<>();

            response.setMsgHeader(new ErrorResponse("1"));
            response.setMsgBody(null);

            return response;
        }
    }

    private com.m2e.dashboard.model.AccountInquiry.Request.MsgWraper buildESBRequest(
            UserAndCorp user,
            DepositAccountDetailReqDTO reqFE) {

        com.m2e.dashboard.model.AccountInquiry.Request.MsgWraper request =
                new com.m2e.dashboard.model.AccountInquiry.Request.MsgWraper();

        request.getMsg().getMsgHeader().setMsgID(
                UUID.randomUUID().toString());

        request.getMsg().getMsgHeader().setVer("01");
        request.getMsg().getMsgHeader().setSvcID("IDINQACCTINF001");
        request.getMsg().getMsgHeader().setTxnCode("IMSTXLI1");
        request.getMsg().getMsgHeader().setEnv("S");
        request.getMsg().getMsgHeader().setHostID("SYSTEMATIC");


        request.getMsg().getMsgBody().setAccountNo(
                reqFE.getAcctNumber());

        request.getMsg().getMsgBody().setAccountBranchCode(
                "003");

        request.getMsg().getMsgBody().setAccountCurrency(
                reqFE.getCurrency());

        return request;
    }

    private DepositAccountDetailDTO mapEsbToDepositAccountDetail(
            MsgWraper response) {

        var accountInfo =
                response.getMsg()
                        .getMsgBody()
                        .getAccountInformationResponseData();

        var caRecord =
                accountInfo.getAccountData()
                        .getcADataRecord();

        DepositAccountDetailDTO dto =
                new DepositAccountDetailDTO();

        dto.setAcctName(
                accountInfo.getAccountName());

        dto.setCompanyName(
                caRecord.getCifName());

        dto.setAcctNumber(
                caRecord.getAcctNumber());

        dto.setCurrency(
                caRecord.getAcctCcy());

        dto.setLedgerBalance(
                cleanAmount(caRecord.getLedgerAmount()));

        dto.setAvailableBalance(
                cleanAmount(caRecord.getAvailableBalance()));

        dto.setActualBalance(
                cleanAmount(caRecord.getEndingBalance()));

        dto.setHoldAmount(
                cleanAmount(caRecord.getHoldAmount()));

        dto.setOdLimit(
                cleanAmount(caRecord.getOverDraftLimit()));

        dto.setUtilisedOD(
                cleanAmount(caRecord.getUtilisedAmount()));

        dto.setUnUtilisedOD(
                cleanAmount(caRecord.getUnutilisedAmount()));

        dto.setProductName(
                caRecord.getProductName());

        dto.setAcctType(
                caRecord.getAccountType());

        dto.setAcctStatusDesc(
                accountInfo.getAccountStatusDesc());

        dto.setIsIslamic(
                caRecord.getIslamicFlag());

        dto.setLastUpdatedDateTime(
                LocalDateTime.now()
                        .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));


        // Field yang belum tersedia dari ESB


        dto.setAuthODLimit("0.00");
        dto.setIntraDayODLimit("0.00");
        dto.setUnUtilisedIntraDayOD("0.00");

        dto.setOmnibusODLimit("0.00");
        dto.setUnUtilisedOmnibusOD("0.00");

        dto.setOneDayFloat("0.00");
        dto.setTwoDayFloat("0.00");
        dto.setOtherDayFloat("0.00");
        dto.setTotalFloat("0.00");

        return dto;
    }

    private String cleanAmount(String amount) {

        if (amount == null) {
            return null;
        }

        return amount
                .replace("+", "")
                .trim();
    }
}