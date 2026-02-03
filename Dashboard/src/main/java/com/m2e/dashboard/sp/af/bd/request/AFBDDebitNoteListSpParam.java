package com.m2e.dashboard.sp.af.bd.request;

import com.m2e.dashboard.dto.common.BasePageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AFBDDebitNoteListSpParam extends BasePageRequest {
    private String debitNoteNumber;
    private String blockNumber;
    private String userId;
    private String corpId;
}