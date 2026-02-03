package com.m2e.dashboard.dto.otherbank.request.deposit;

import com.m2e.dashboard.enums.MessageType;
import lombok.Data;

import java.io.Serializable;

@Data
public class OtherBankDepositAcctDetailReqDTO implements Serializable {
    private MessageType messageType;
    private String acctNumber;
}
