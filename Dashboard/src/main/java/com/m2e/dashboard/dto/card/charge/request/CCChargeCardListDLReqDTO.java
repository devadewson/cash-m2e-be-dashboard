package com.m2e.dashboard.dto.card.charge.request;

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
public class CCChargeCardListDLReqDTO extends DownloadReq {
    private String openTaggedCorpId;
    private String searchParam;
}
