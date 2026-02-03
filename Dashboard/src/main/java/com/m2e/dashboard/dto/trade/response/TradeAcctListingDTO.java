package com.m2e.dashboard.dto.trade.response;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.trade.response.TradeAcctListingSpDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true)
public class TradeAcctListingDTO {
    private String corpCifNo;      // mandatory attribute based on FE requirement to fix accordion problem
    private SearchCollectionResponse<TradeAcctListingSpDTO> datas;
}