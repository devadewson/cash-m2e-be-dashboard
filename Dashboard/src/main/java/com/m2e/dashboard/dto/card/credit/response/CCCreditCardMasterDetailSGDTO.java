package com.m2e.dashboard.dto.card.credit.response;

import com.m2e.dashboard.dto.card.common.response.CCBaseCardDetailDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class CCCreditCardMasterDetailSGDTO extends CCBaseCardDetailDTO {
    private String availableCreditLimit;
    private String totalCreditLimit;
    private String rewardPoints;
}
