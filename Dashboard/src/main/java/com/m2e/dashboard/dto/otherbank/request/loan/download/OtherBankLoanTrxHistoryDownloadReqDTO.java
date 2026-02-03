package com.m2e.dashboard.dto.otherbank.request.loan.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.otherbank.request.OtherBankTrxHistoryReqDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class OtherBankLoanTrxHistoryDownloadReqDTO extends DownloadReq {
    private String acctNumber;
    private String trxCode;
    private String searchParam;
    private OtherBankTrxHistoryReqDTO filters;
    private String msgType;
    private String[] headers = {"Date", "Transaction Reference", "Transaction Description", "Debit", "Credit"};
}
