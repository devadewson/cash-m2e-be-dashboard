package com.m2e.dashboard.mapper.einvoice;

import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceStatementListingReqDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMInvoiceDTO;
import com.m2e.dashboard.dto.einvoice.response.EIMStatementDTO;
import com.m2e.dashboard.dto.einvoice.response.EInvoiceStatementDTO;
import com.m2e.dashboard.dto.einvoice.response.EInvoiceStatementListingDTO;
import com.m2e.dashboard.projection.AcctDetailPageProjection;
import com.m2e.dashboard.repository.AccountRepository;
import com.m2e.dashboard.repository.CorpRepository;
import com.m2e.dashboard.service.DropdownService;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Mapper(componentModel = "spring")
public abstract class EInvoiceStatementMapper {

    @Autowired
    protected CorpRepository corpRepository;
    @Autowired
    protected AccountRepository accountRepository;
    @Autowired
    protected DropdownService dropdownService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);

    @Mapping(target = "totalPages", expression = "java(calculateTotalPages(dto.getTotal(), dto.getPageSize()))")
    @Mapping(source = "pageNo", target = "currentPage")
    @Mapping(expression = "java(dto.getHasNextPage().equalsIgnoreCase(\"true\") ? dto.getPageNo() + 1 : -1)", target = "nextPage")
    @Mapping(source = "total", target = "totalRecordCount")
    @Mapping(source = "invoiceList", target = "data")
    public abstract SearchCollectionResponse<EInvoiceStatementDTO> toSearchCollection(EIMStatementDTO dto);

    @Mapping(source = "dateTimeIssued", target = "stmtDate")
    @Mapping(source = "accountNumber", target = "acctNumber")
    @Mapping(source = "currencyCode", target = "currency")
    public abstract EInvoiceStatementDTO toEInvoiceStatement(EIMInvoiceDTO invoice);

    @AfterMapping
    protected void enrichInvoiceDTO(EIMInvoiceDTO source, @MappingTarget EInvoiceStatementDTO target) {
        String acctName = getAccountName(source.getAccountNumber(), source.getCurrencyCode());
        target.setAcctName(acctName);

        if (source.getDateTimeIssued() != null) {
            try {
                OffsetDateTime dateTime = OffsetDateTime.parse(source.getDateTimeIssued());
                target.setStmtDate(FORMATTER.format(dateTime));
            } catch (Exception e){
                target.setStmtDate(null);
            }
        }
    }

    public EInvoiceStatementListingDTO toStatementListing(EIMStatementDTO dto, String corpId, EInvoiceStatementListingReqDTO reqDTO) {
        SearchCollectionResponse<EInvoiceStatementDTO> collection = toSearchCollection(dto);
        boolean isSME = isSMEbyCorpId(corpId);

        collection.getData().forEach(item -> {
            // CR : STATEMENT DATE = BILL DATE
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
            LocalDate startDate = LocalDate.parse(reqDTO.getStartDate(), formatter);
            LocalDate endDate = LocalDate.parse(reqDTO.getStartDate(), formatter);
            item.setStmtDate(
                    dropdownService.describePeriod(
                            startDate.format(formatter),
                            endDate.format(formatter)
                    )
            );
            item.setIsSME(isSME);
            String acctName = getAccountName(reqDTO.getAcctNumber(), item.getCurrency());
            item.setAcctName(acctName);
            item.setAcctNumber(reqDTO.getAcctNumber());});

        return new EInvoiceStatementListingDTO(collection);
    }

    protected Integer calculateTotalPages(Integer total, Integer pageSize) {
        if (total == null || pageSize == null || pageSize == 0) return 0;
        return (int) Math.ceil((double) total / pageSize);
    }

    public boolean isSMEbyCorpId(String corpId) {
        String businessSegmentCode = corpRepository.getMtBusinessSegCodeByCorpId(corpId).orElse(null);
        return StringUtils.hasText(businessSegmentCode) && "CFS".equalsIgnoreCase(businessSegmentCode);
    }

    public String getAccountName(String acctNumber, String currency) {
        return accountRepository.findAccountDetails(acctNumber, currency)
                .map(acctDetail -> {
                    String name = acctDetail.getAcctPrefNm();
                    return (name != null && !name.isBlank())
                            ? name
                            : acctDetail.getProductName();
                })
                .orElse(null);
    }

    public abstract List<EInvoiceStatementDTO> toEInvoiceStatementList(List<EIMInvoiceDTO> list);
}
