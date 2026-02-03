package com.m2e.dashboard.dto.deposit.fd.request.download;

import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.enums.ProviderEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DepositFDCertificateListingDownloadReqDTO extends DownloadReq {
    private String acctNumber;
    private String searchParam;
    private String currency;
    private ProviderEnum provider;
    private String mbbLastKey;
}