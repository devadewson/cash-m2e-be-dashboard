package com.m2e.dashboard.dto.einvoice.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class EInvoiceValidateTINReqDTO implements Serializable {

    private String tin;
    private String brn;

}
