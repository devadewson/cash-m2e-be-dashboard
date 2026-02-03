package com.m2e.dashboard.dto.loan.overdraft.request;

import com.m2e.dashboard.dto.base.DownloadReq;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LNOverdraftAcctListingDownloadReqDTO extends DownloadReq {
    private String corpCifNo;
    private String searchParam;
}