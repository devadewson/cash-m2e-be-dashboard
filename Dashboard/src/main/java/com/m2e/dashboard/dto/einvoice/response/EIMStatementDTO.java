package com.m2e.dashboard.dto.einvoice.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EIMStatementDTO implements Serializable {
    private Integer pageNo;
    private Integer pageSize;
    private Integer total;
    private String hasNextPage;
    private List<EIMInvoiceDTO> invoiceList = new ArrayList<>();
}
