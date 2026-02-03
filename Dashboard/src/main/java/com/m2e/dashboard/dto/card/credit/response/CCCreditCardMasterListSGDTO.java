package com.m2e.dashboard.dto.card.credit.response;

import com.m2e.dashboard.dto.card.common.response.CCAccListBaseDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
public class CCCreditCardMasterListSGDTO extends CCAccListBaseDTO {
    private String noOfCardMembers;
}
