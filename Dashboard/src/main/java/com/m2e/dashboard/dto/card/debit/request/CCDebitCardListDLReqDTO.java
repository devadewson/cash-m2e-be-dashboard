package com.m2e.dashboard.dto.card.debit.request;

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
public class CCDebitCardListDLReqDTO extends DownloadReq {
    private String openTaggedCorpId;
    private ProviderEnum provider;
}
