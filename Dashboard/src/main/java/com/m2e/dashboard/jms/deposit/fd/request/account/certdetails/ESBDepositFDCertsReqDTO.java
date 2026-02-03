package com.m2e.dashboard.jms.deposit.fd.request.account.certdetails;

import com.m2e.dashboard.enums.ProviderEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class ESBDepositFDCertsReqDTO {
    private String accountNo;
    private String searchParam;
    private String currency;
    private ProviderEnum provider;
    private String mbbLastKey;
    private String accountType;
    private int page;
    private int pageSize;
}