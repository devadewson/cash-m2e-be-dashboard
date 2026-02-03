package com.m2e.dashboard.dto.card.credit.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class CCCreditCardMasterListReqDTO extends BasePageRequest {
    private String openTaggedCorpId;
}
