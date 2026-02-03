package com.m2e.dashboard.dto.loan.tlf.request.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.dto.loan.tlf.request.LNTrxHistoryFilterDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class LNTLFTranchesDownloadReqDTO extends DownloadReq {
    private String acctNumber;
    private String accountCurrency;
    private String openTaggedCorpId;
    private String searchParam;
}
