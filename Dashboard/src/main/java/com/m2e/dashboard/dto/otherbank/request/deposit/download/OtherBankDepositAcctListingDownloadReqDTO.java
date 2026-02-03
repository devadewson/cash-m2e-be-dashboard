package com.m2e.dashboard.dto.otherbank.request.deposit.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.enums.OtherBankViewType;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OtherBankDepositAcctListingDownloadReqDTO extends DownloadReq {
    private String corpCifNo;
    private String searchParam;
    private OtherBankViewType viewType;
}
