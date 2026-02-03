package com.m2e.dashboard.dto.af.request.bd;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.Data;

@Data
public class AFBDDebitNoteListReqDTO extends BasePageRequest {
    private String debitNoteNumber;
    private String blockNumber;
}
