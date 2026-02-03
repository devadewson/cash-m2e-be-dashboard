package com.m2e.dashboard.dto.otherbank.request.loan.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.enums.OtherBankViewType;
import lombok.Data;

@Data
public class OtherBankLoanAcctListDownloadReqDTO extends DownloadReq {
    private String searchParam;
    private String corpCifNo;
    private OtherBankViewType viewType;
}
