package com.m2e.dashboard.dto.otherbank.request.deposit.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.otherbank.request.OtherBankTrxHistoryReqDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OtherBankDepositTrxHistoryDownloadReqDTO extends DownloadReq {
    private String acctNumber;
    private String trxCode;
    private String searchParam;
    private OtherBankTrxHistoryReqDTO filters;
    private String msgType;
    private String[] headers;
}
