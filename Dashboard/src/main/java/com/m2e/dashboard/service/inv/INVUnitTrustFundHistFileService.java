package com.m2e.dashboard.service.inv;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.dto.investment.response.INVUnitTrustTransHistSearchDTO;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.*;
import java.util.function.Function;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
public class INVUnitTrustFundHistFileService implements FileService<List<INVUnitTrustTransHistSearchDTO>> {
    private static final Logger log = LoggerFactory.getLogger(INVUnitTrustFundHistFileService.class);
    String fundNameStr = "Fund Name";
    String description = "Description";
    String lastClosingNav = "Last Closing NAV";
    String noofUnits = "Units";
    String debitAmount = "Debit";
    String creditAmount = "Credit";
    String[] headers = new String[]{fundNameStr, description, lastClosingNav, noofUnits, debitAmount, creditAmount};
    @Override
    public byte[] generateCsv(List<INVUnitTrustTransHistSearchDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getFundName() != null ? dto.getFundName() : "",
                            dto.getTrxDesc() != null ? dto.getTrxDesc() : "",
                            String.valueOf(dto.getNav() != null ? dto.getNav() : 0),
                            String.valueOf(dto.getUnits() != null ? dto.getUnits() : 0),
                            dto.getDebit() != null ? dto.getDebit() : "-",
                            dto.getCredit() != null ? dto.getCredit() : "-"
                    })
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("generateCsv: {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<INVUnitTrustTransHistSearchDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<INVUnitTrustTransHistSearchDTO, String>> columnGetters = Arrays.stream(headers)
                    .map(header -> this.getHeaderMap().get(header))
                    .toList();
            List<String> columns = new ArrayList<>(this.getHeaderMap().keySet());

            float totalTableWidth = calculateTotalHeadersTableWidth(columns, columnGetters);
            PageSize pageSize = getOptimalPageSize(totalTableWidth);

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setDefaultPageSize(pageSize);
            Document document = new Document(pdf);

            float[] columnWidths = calculateHeadersColumnWidths(columns, columnGetters, pageSize.getWidth());
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            Arrays.stream(headers)
                    .forEach(header -> table.addHeaderCell(new Cell().add(new Paragraph(header))));
            datas.forEach(row -> Arrays.stream(headers).forEach(
                    header -> {
                        Function<INVUnitTrustTransHistSearchDTO, String> function = this.getHeaderMap().get(header);
                        String cellValue = function != null && function.apply(row) != null ? function.apply(row) : "-";
                        table.addCell(new Cell().add(new Paragraph(cellValue)));
                    }));
            document.add(new Paragraph("Investment Transaction History Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<INVUnitTrustTransHistSearchDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-100s%-60s%-20s%-20s%-25s%-25s\r\n",
                fundNameStr, description, lastClosingNav, noofUnits, debitAmount, creditAmount));
        DecimalFormat df = new DecimalFormat("0.00");

        datas.forEach(row -> {
            String fundName = row.getFundName() != null ? row.getFundName() : "";
            String transactionDescription = row.getTrxDesc() != null ? row.getTrxDesc() : "";
            String nav = row.getNav() != null ? row.getNav() : "";
            String units = row.getUnits() != null ? row.getUnits() : "";
            String debit = row.getDebit() != null ? row.getDebit() : "-";
            String credit = row.getCredit() != null ? row.getCredit() : "-";

            String formattedRow = String.format("%-100s%-60s%-20s%-20s%-25s%-25s",
                truncateOrPad(fundName, 100),
                truncateOrPad(transactionDescription, 60),
                truncateOrPad(nav, 20),
                truncateOrPad(units, 20),
                truncateOrPad(debit, 25),
                truncateOrPad(credit, 25));
            sb.append(formattedRow).append("\r\n");
        });

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String truncateOrPad(String input, int length) {
        if (input.length() > length) {
            return input.substring(0, length);
        } else {
            String formatString = "%-" + length + "s";
            return String.format(formatString, input);
        }
    }

    private Map<String, Function<INVUnitTrustTransHistSearchDTO, String>> getHeaderMap() {
        Map<String, Function<INVUnitTrustTransHistSearchDTO, String>> headerMap = new HashMap<>();
        headerMap.put("Date", INVUnitTrustTransHistSearchDTO::getTransactionDate);
        headerMap.put("Fund Name", INVUnitTrustTransHistSearchDTO::getFundName);
        headerMap.put("Description", INVUnitTrustTransHistSearchDTO::getTrxDesc);
        headerMap.put("Last Closing NAV", INVUnitTrustTransHistSearchDTO::getNav);
        headerMap.put("Units", INVUnitTrustTransHistSearchDTO::getUnits);
        headerMap.put("Debit", INVUnitTrustTransHistSearchDTO::getDebit);
        headerMap.put("Credit", INVUnitTrustTransHistSearchDTO::getCredit);
        return headerMap;
    }

}
