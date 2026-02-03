package com.m2e.dashboard.dto.otherbank.response.common;

import lombok.Data;

import java.util.List;

@Data
public class OtherBankAcctDetailSpecialListDTO extends OtherBankAcctDetailDTO {
    private List<OtherBankAcctDetailSpecialDTO> statementDetail;
}
