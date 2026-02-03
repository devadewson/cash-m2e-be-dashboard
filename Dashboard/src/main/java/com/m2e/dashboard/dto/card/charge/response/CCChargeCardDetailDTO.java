package com.m2e.dashboard.dto.card.charge.response;

import com.m2e.dashboard.dto.card.common.response.CCBaseCardDetailDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class CCChargeCardDetailDTO extends CCBaseCardDetailDTO {
    private String cardHolderName;
    private String statementBalance;
    private String lastPaymentDate;
    private String statementDate;
    private String dueDate;
    private String lastPaymentAmount;
}
