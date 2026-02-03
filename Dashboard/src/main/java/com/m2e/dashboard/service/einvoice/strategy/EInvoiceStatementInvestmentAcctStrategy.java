package com.m2e.dashboard.service.einvoice.strategy;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.einvoice.response.EInvoiceStatementAcctDTO;
import com.m2e.dashboard.dto.maintenance.request.AcctNameListReqDTO;
import com.m2e.dashboard.dto.maintenance.response.MTINVUnitTrustSearchDTO;
import com.m2e.dashboard.enums.SubProductEnum;
import com.m2e.dashboard.service.maintenance.MTINVUnitTrustService;
import com.m2e.dashboard.sp.maintenance.response.MTINVUnitTrustSearchSpDTO;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class EInvoiceStatementInvestmentAcctStrategy implements EInvoiceStatementAcctStrategy {

    private final MTINVUnitTrustService mtinvUnitTrustService;
    private final ModelMapper modelMapper;

    private static final Set<String> SUPPORTED = Set.of(SubProductEnum.UNIT_TRUST.getCode());

    @Override
    public boolean supports(String acctType) {
        return SUPPORTED.contains(acctType);
    }

    @Override
    public ApiResponse<List<EInvoiceStatementAcctDTO>> execute(UserAndCorp auth, String acctType, String searchParam) {
        AcctNameListReqDTO req = new AcctNameListReqDTO();
        req.setSearchParam(req.getSearchParam());
        req.setSubProduct(acctType);
        req.setPage(1);
        req.setPageSize(Integer.MAX_VALUE);

        ApiResponse<MTINVUnitTrustSearchDTO> resp = mtinvUnitTrustService.getINVUTAcctLists(auth, req);
        if (!"0".equalsIgnoreCase(resp.getMsgHeader().getResponseCode())) {
            return new ApiResponse().buildError("500", "Investment service failed");
        }

        List<MTINVUnitTrustSearchSpDTO> sourceList = Optional.ofNullable(resp.getMsgBody())
            .map(MTINVUnitTrustSearchDTO::getDatas)
            .map(SearchCollectionResponse::getData)
            .orElse(Collections.emptyList());

        List<EInvoiceStatementAcctDTO> result = sourceList.stream()
            .map(dto -> modelMapper.map(dto, EInvoiceStatementAcctDTO.class))
            .collect(Collectors.toList());

        return new ApiResponse<>().buildSuccess(result);
    }
}
