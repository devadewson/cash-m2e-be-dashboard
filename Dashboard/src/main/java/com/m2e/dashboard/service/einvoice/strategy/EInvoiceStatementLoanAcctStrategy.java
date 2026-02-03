package com.m2e.dashboard.service.einvoice.strategy;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.einvoice.response.EInvoiceStatementAcctDTO;
import com.m2e.dashboard.dto.maintenance.request.AcctNameListReqDTO;
import com.m2e.dashboard.dto.maintenance.response.MTLNSearchDTO;
import com.m2e.dashboard.enums.SubProductEnum;
import com.m2e.dashboard.service.maintenance.MTLNService;
import com.m2e.dashboard.sp.maintenance.response.MTLNSearchSpDTO;
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
public class EInvoiceStatementLoanAcctStrategy implements EInvoiceStatementAcctStrategy {

    private final MTLNService mtLoanTlfService;
    private final ModelMapper modelMapper;

    private static final Set<String> SUPPORTED = Set.of(
        SubProductEnum.OVERDRAFT.getCode(),
        SubProductEnum.TERM_LOAN.getCode(),
        SubProductEnum.REVOLVING_CREDIT.getCode()
    );

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

        ApiResponse<MTLNSearchDTO> resp = mtLoanTlfService.getLoanTLFAcctList(auth, req);
        if (!"0".equalsIgnoreCase(resp.getMsgHeader().getResponseCode())) {
            return new ApiResponse().buildError("500", "Loan service failed");
        }

        List<MTLNSearchSpDTO> sourceList = Optional.ofNullable(resp.getMsgBody())
            .map(MTLNSearchDTO::getDatas)
            .map(SearchCollectionResponse::getData)
            .orElse(Collections.emptyList());

        List<EInvoiceStatementAcctDTO> result = sourceList.stream()
            .map(dto -> modelMapper.map(dto, EInvoiceStatementAcctDTO.class))
            .collect(Collectors.toList());

        return new ApiResponse<>().buildSuccess(result);
    }
}
