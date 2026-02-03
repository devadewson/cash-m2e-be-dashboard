package com.m2e.dashboard.dto.af.request.fs.download;

import com.m2e.dashboard.dto.af.request.AFTrxHistoryFilterDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.enums.ProviderEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(description = "Request for Hire Purchase transaction history download", allOf = {DownloadReq.class})
public class AFFSTrxHistDownloadReqDTO extends DownloadReq {
    private String provider;
    private String acctNumber;
    private String currency;
    private String searchParam;
    @Schema(
            description = "Headers for the downloaded file",
            example = "[\"Date\", \"Transaction Reference\", \"Transaction Description\", \"Debit\", \"Credit\", " +
                    "\"Processing Time\", \"Transaction Description 2\", \"Transaction Description 3\", " +
                    "\"Transaction Description 4\", \"Virtual Account\", \"Transaction Status\", \"Transaction Type\", " +
                    "\"Source Code\", \"Teller ID\", \"Owner's Institution\", \"Transaction Code\", " +
                    "\"Auxiliary Transaction Code\", \"Expiry Date\", \"Currency\"]"
    )
    private String[] headers;
    private AFTrxHistoryFilterDTO filters;
}
