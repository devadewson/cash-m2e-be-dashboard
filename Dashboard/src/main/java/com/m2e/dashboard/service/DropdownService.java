package com.m2e.dashboard.service;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceStatementAcctReqDTO;
import com.m2e.dashboard.dto.einvoice.response.EInvoiceStatementAcctDTO;
import com.m2e.dashboard.dto.einvoice.response.EInvoiceStatementPeriodDTO;
import com.m2e.dashboard.dto.maintenance.request.SubProductListReqDTO;
import com.m2e.dashboard.dto.maintenance.response.ProductDTO;
import com.m2e.dashboard.dto.maintenance.response.SubProductDTO;
import com.m2e.dashboard.enums.FrequencyEnum;
import com.m2e.dashboard.enums.Product;
import com.m2e.dashboard.enums.SubProductEnum;
import com.m2e.dashboard.service.einvoice.strategy.EInvoiceStatementStrategyFactory;
import com.m2e.dashboard.service.maintenance.MTAcctNameService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Month;
import java.time.Year;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.TextStyle;
import java.util.*;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class DropdownService {

    private static final Logger log = LoggerFactory.getLogger(DropdownService.class);

    private final MTAcctNameService mtAcctNameService;
    private final EInvoiceStatementStrategyFactory strategyFactory;

    public ApiResponse<List<ProductDTO>> getEInvoiceStatementProducts() {
        List<Product> excludedProducts = List.of(Product.TRADE, Product.INVESTMENT, Product.OTHER_BANK_ACCOUNT);

        List<ProductDTO> productDTOList = Arrays.stream(Product.values())
                .filter(product -> !excludedProducts.contains(product))
                .map(this::constructProductDTO)
                .toList();

        return new ApiResponse<>(new ErrorResponse("0"), productDTOList);
    }

    public ProductDTO constructProductDTO(Product product) {
        return new ProductDTO(product.name(), product.getValue());
    }
    public ApiResponse<List<SubProductDTO>> getEInvoiceStatementAcctTypesByProduct(SubProductListReqDTO req) {
        Product product = Optional.ofNullable(req)
                .map(SubProductListReqDTO::getProductCode)
                .orElse(null);

        if (product == null) {
            log.info("Product code must not be null");
            return new ApiResponse<>(new ErrorResponse("99"), null);
        }

        List<SubProductDTO> result = Product.ALL.equals(product)
                ? getEInvoiceStatementAcctTypes().getMsgBody()
                : mtAcctNameService.findSubProducts(product);

        return new ApiResponse<>(new ErrorResponse("0"), result);

    }


    public ApiResponse<List<SubProductDTO>> getEInvoiceStatementAcctTypes() {
        List<Product> excludedProducts = List.of(Product.CARD, Product.TRADE, Product.INVESTMENT, Product.OTHER_BANK_ACCOUNT);
        List<SubProductDTO> aggregatedSubProducts = Arrays.stream(Product.values())
                .filter(product -> !excludedProducts.contains(product))
                .map(mtAcctNameService::findSubProducts)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(subProduct -> !SubProductEnum.OVERDRAFT.getCode().equals(subProduct.getSubProductCode()))
                .toList();

        return new ApiResponse<>(new ErrorResponse("0"), aggregatedSubProducts);
    }

    public ApiResponse<List<EInvoiceStatementAcctDTO>> getEInvoiceStatementAccounts(UserAndCorp auth, EInvoiceStatementAcctReqDTO req) {
        return strategyFactory.resolve(req.getAcctType())
                .map(strategy -> strategy.execute(auth, req.getAcctType(), req.getSearchParam()))
                .orElseGet(() -> {
                    log.info("Unidentified account type -> {}", req.getAcctType());
                    return new ApiResponse().buildError("500", "Unidentified account type");
                });
    }

    public ApiResponse<List<String>> getEinvoiceStatementYears() {
        int currentYear = Year.now().getValue();
        ApiResponse<List<String>> response = new ApiResponse<>();
        response.setMsgHeader(new ErrorResponse());

        // TODO: [PIC::Weixian] Replace retentionYears with value queried from the database
        int retentionYears = 2;
        response.setMsgBody(IntStream.rangeClosed(currentYear - retentionYears + 1, currentYear)
                .mapToObj(String::valueOf)
                .sorted(Comparator.reverseOrder())
                .toList());
        return response;
    }

    public ApiResponse<List<EInvoiceStatementPeriodDTO>> generateStatementPeriods(FrequencyEnum frequency, int year) {
        List<EInvoiceStatementPeriodDTO> periods = switch (frequency) {
            case MONTHLY -> IntStream.rangeClosed(1, 12)
                    .mapToObj(month -> {
                        String description = Month.of(month).getDisplayName(TextStyle.FULL, Locale.ENGLISH);
                        LocalDate start = LocalDate.of(year, month, 1);
                        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
                        return new EInvoiceStatementPeriodDTO(
                                String.valueOf(month),
                                year,
                                description,
                                toLongDate(start),
                                toLongDate(end)
                        );
                    })
                    .toList();

            case QUARTERLY -> List.of(
                    buildPeriod("1-3", year, "January - March ", 1, 3),
                    buildPeriod("4-6", year, "April - June ", 4, 6),
                    buildPeriod("7-9", year, "July - September ", 7, 9),
                    buildPeriod("10-12", year, "October - December ", 10, 12)
            );

            case HALF_YEARLY -> List.of(
                    buildPeriod("1-6", year, "January - June", 1, 6),
                    buildPeriod("7-12", year, "July - December", 7, 12)
            );

            case YEARLY -> List.of(
                    buildPeriod("1-12", year, "January - December", 1, 12)
            );

            case CUSTOMISE -> List.of(
                    new EInvoiceStatementPeriodDTO("Custom", year, "Custom date range", null, null)
            );
        };

        return new ApiResponse<>(new ErrorResponse(), periods);
    }

    public String describePeriod(String startDate, String endDate) {
        DateTimeFormatter IN  = DateTimeFormatter.ofPattern("yyyyMMdd");
        DateTimeFormatter OUT = DateTimeFormatter.ofPattern("MMM yyyy", Locale.ENGLISH);

        LocalDate s = LocalDate.parse(startDate, IN);
        LocalDate e = LocalDate.parse(endDate, IN);

        // Ensure s <= e (optional safety)
        if (e.isBefore(s)) {
            LocalDate tmp = s; s = e; e = tmp;
        }

        YearMonth ys = YearMonth.from(s);
        YearMonth ye = YearMonth.from(e);

        // Same month => "Feb 2025"
        if (ys.equals(ye)) {
            return ys.format(OUT);
        }

        // Different months => "Feb 2025 - May 2025"
        return ys.format(OUT) + " - " + ye.format(OUT);
    }

    public static DateRange parseToYyyyMMddRange(String text) {
        DateTimeFormatter out = DateTimeFormatter.ofPattern("yyyyMMdd");
        String input = normalizeDashes(text).trim();

        // Yearly
        if (input.matches("^\\d{4}$")) {
            int year = Integer.parseInt(input);
            return new DateRange(
                    out.format(LocalDate.of(year, 1, 1)),
                    out.format(LocalDate.of(year, 12, 31))
            );
        }

        // Quarterly
        if (input.matches("(?i)^Q[1-4] \\d{4}$")) {
            int quarter = Integer.parseInt(input.substring(1, 2));
            int year = Integer.parseInt(input.substring(3).trim());
            int startMonth = (quarter - 1) * 3 + 1;
            YearMonth start = YearMonth.of(year, startMonth);
            YearMonth end = start.plusMonths(2);
            return new DateRange(
                    out.format(start.atDay(1)),
                    out.format(end.atEndOfMonth())
            );
        }

        // Month range: "Jan - Jun 2025" / "January - June 2025"
        if (input.matches("(?i)^[A-Za-z]{3,9} - [A-Za-z]{3,9} \\d{4}$")) {
            String[] parts = input.split(" ");
            Month startMonth = parseMonthFlexible(parts[0]);
            Month endMonth = parseMonthFlexible(parts[2]);
            int year = Integer.parseInt(parts[3]);
            YearMonth start = YearMonth.of(year, startMonth);
            YearMonth end = YearMonth.of(year, endMonth);
            return new DateRange(
                    out.format(start.atDay(1)),
                    out.format(end.atEndOfMonth())
            );
        }

        // Single month: "Jul 2025" / "July 2025"
        if (input.matches("(?i)^[A-Za-z]{3,9} \\d{4}$")) {
            String[] parts = input.split(" ");
            Month month = parseMonthFlexible(parts[0]);
            int year = Integer.parseInt(parts[1]);
            YearMonth ym = YearMonth.of(year, month);
            return new DateRange(
                    out.format(ym.atDay(1)),
                    out.format(ym.atEndOfMonth())
            );
        }

        throw new IllegalArgumentException("Unrecognized period format: " + text);
    }

    private static Month parseMonthFlexible(String month) {
        try {
            return Month.from(DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH).parse(month));
        } catch (DateTimeParseException e) {
            return Month.from(DateTimeFormatter.ofPattern("MMM", Locale.ENGLISH).parse(month));
        }
    }

    private static String normalizeDashes(String s) {
        return s.replace('–', '-').replace('—', '-');
    }

    public record DateRange(String startDate, String endDate) {}

    public ApiResponse<List<SubProductDTO>> getEInvoiceDownloadAcctTypes() {
        List<Product> excludedProducts = List.of(Product.TRADE, Product.INVESTMENT, Product.OTHER_BANK_ACCOUNT);
        List<SubProductDTO> aggregatedSubProducts = Arrays.stream(Product.values())
                .filter(product -> !excludedProducts.contains(product))
                .map(mtAcctNameService::findSubProductsEInvoiceDL)
                .filter(Objects::nonNull)
                .flatMap(List::stream)
                .filter(subProduct -> !SubProductEnum.DEBIT_CARD.getCode().equals(subProduct.getSubProductCode()))
                .toList();

        return new ApiResponse<>(new ErrorResponse("0"), aggregatedSubProducts);
    }

    private Long toLongDate(LocalDate date) {
        return Long.valueOf(date.format(DateTimeFormatter.BASIC_ISO_DATE));
    }

    private EInvoiceStatementPeriodDTO buildPeriod(String code, int year, String description, int startMonth, int endMonth) {
        LocalDate start = LocalDate.of(year, startMonth, 1);
        LocalDate end = LocalDate.of(year, endMonth, YearMonth.of(year, endMonth).lengthOfMonth());
        return new EInvoiceStatementPeriodDTO(code, year, description, toLongDate(start), toLongDate(end));
    }


}
