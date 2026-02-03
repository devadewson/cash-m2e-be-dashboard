package com.m2e.dashboard.dto.einvoice.response;

import com.m2e.dashboard.enums.FrequencyEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EInvoiceMonthReqDTO implements Serializable {
    private FrequencyEnum frequencyEnum;
    private String year;
}
