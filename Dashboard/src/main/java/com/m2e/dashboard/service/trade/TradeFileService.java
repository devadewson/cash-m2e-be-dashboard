package com.m2e.dashboard.service.trade;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.service.FileService;
import com.m2e.dashboard.service.af.pbr.AFPBRTrxHistoryFileService;
import com.m2e.dashboard.sp.trade.response.TradeAcctListingSpDTO;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
public class TradeFileService implements FileService<List<TradeAcctListingSpDTO>> {

    private static final Logger log = LoggerFactory.getLogger(TradeFileService.class);
    @Override
    public byte[] generateCsv(List<TradeAcctListingSpDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            // TODO : modify this logic based on real report format
            String[] headers = {"Bill Name", "Items", "Currency", "Outstanding Balance"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getAcctName() != null ? dto.getAcctName() : "",
                            dto.getNumberOfItems() != null ? dto.getNumberOfItems()  : "",
                            dto.getCurrency() != null ? dto.getCurrency() : "",
                            dto.getOutstandingBalance() != null ? dto.getOutstandingBalance() : ""
                    })
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("generateCsv: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<TradeAcctListingSpDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<TradeAcctListingSpDTO, String>> columnGetters = Arrays.asList(
                    TradeAcctListingSpDTO::getAcctName,
                    TradeAcctListingSpDTO::getNumberOfItems,
                    TradeAcctListingSpDTO::getCurrency,
                    TradeAcctListingSpDTO::getOutstandingBalance
            );
            float totalTableWidth = calculateTotalTableWidth(datas, columnGetters);
            PageSize pageSize = getOptimalPageSize(totalTableWidth);

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            float[] columnWidths = calculateColumnWidths(datas, columnGetters, pageSize.getWidth());
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Bill Name")));
            table.addHeaderCell(new Cell().add(new Paragraph("Items")));
            table.addHeaderCell(new Cell().add(new Paragraph("Currency")));
            table.addHeaderCell(new Cell().add(new Paragraph("Outstanding Balance")));

            datas.stream().forEach(row -> {
                table.addCell(new Cell().add(new Paragraph(row.getAcctName())));
                table.addCell(new Cell().add(new Paragraph(row.getNumberOfItems())));
                table.addCell(new Cell().add(new Paragraph(row.getCurrency())));
                table.addCell(new Cell().add(new Paragraph(row.getOutstandingBalance())));
            });

            Paragraph title = new Paragraph("Trade Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold();
            document.add(title);
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("generatePdf: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<TradeAcctListingSpDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-70s%-10s%-10s%-20s\n",
                "Bill Name", "Items",
                "Currency", "Outstanding Balance"));

        datas.forEach(row -> {
            String acctName = row.getAcctName() != null ? row.getAcctName() : "";
            String numberOfItems = row.getNumberOfItems() != null ? row.getNumberOfItems() : "";
            String currency = row.getCurrency() != null ? row.getCurrency() : "";
            String outstandingBalance = row.getOutstandingBalance() != null ? row.getOutstandingBalance() : "";

            String formattedRow = String.format("%-70s%-10s%-10s%-20s",
                    truncateOrPad(acctName, 70),
                    truncateOrPad(numberOfItems, 5),
                    truncateOrPad(currency, 3),
                    truncateOrPad(outstandingBalance, 20));
            sb.append(formattedRow).append("\n");
        });

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String truncateOrPad(String input, int length) {
        if (input.length() > length) {
            return input.substring(0, length);
        } else {
            return String.format("%-" + length + "s", input);
        }
    }

}
