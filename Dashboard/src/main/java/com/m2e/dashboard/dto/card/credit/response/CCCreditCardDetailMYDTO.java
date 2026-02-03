package com.m2e.dashboard.dto.card.credit.response;

import com.m2e.dashboard.dto.card.common.response.CCBaseCardDetailDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class CCCreditCardDetailMYDTO extends CCBaseCardDetailDTO {
    private String minimumPayment;
    private String statementBalance;
    private String lastPaymentDate;
    private String statementDate;
    private String dueDate;
    private String lastPaymentAmount;
    private String availableCreditLimit;
    private String totalCreditLimit;
}
