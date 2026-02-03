package com.m2e.dashboard.dto.otherbank.response.deposit;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.sp.otherbank.response.OtherBankDepositAcctListingSpDTO;
import lombok.Data;

import java.io.Serializable;

@Data
public class OtherBankDepositAcctListingDTO implements Serializable {
    private SearchCollectionResponse<OtherBankDepositAcctListingSpDTO> datas;
}
