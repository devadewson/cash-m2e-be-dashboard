package com.m2e.dashboard.service.af.pbr;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRViewByAccountDTO;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
public class AFPBRViewByAccountFileService implements FileService<List<AFPBRViewByAccountDTO>> {
    private static final Logger log = LoggerFactory.getLogger(AFPBRViewByAccountFileService.class);
    private static final String[] headers = {"Account Name", "Account Number", "Master Account No", "Currency", "Financed Amount", "Settlement Amount"};

    private static  final Map<String, Function<AFPBRViewByAccountDTO, String>> HEADER_MAP;
    static {
        Map<String, Function<AFPBRViewByAccountDTO, String>> headerMap = new HashMap<>();
        headerMap.put(headers[0], AFPBRViewByAccountDTO::getAcctName);
        headerMap.put(headers[1], AFPBRViewByAccountDTO::getAcctNumber);
        headerMap.put(headers[2], AFPBRViewByAccountDTO::getMasterAcctNumber);
        headerMap.put(headers[3], AFPBRViewByAccountDTO::getAccountCurrency);
        headerMap.put(headers[4], AFPBRViewByAccountDTO::getFinancedAmount);
        headerMap.put(headers[5], AFPBRViewByAccountDTO::getSettlementAmount);
        HEADER_MAP = Collections.unmodifiableMap(headerMap);
    }

    private Map<String, Function<AFPBRViewByAccountDTO, String>> getHeaderMap() {
        return HEADER_MAP;
    }
    
    public byte[] generateCsv(List<AFPBRViewByAccountDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            // TODO : modify this logic based on real report format
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> Arrays.stream(headers)
                            .map(header -> {
                                Function<AFPBRViewByAccountDTO, String> function = this.getHeaderMap().get(header);
                                return function != null && function.apply(dto)!=null ? function.apply(dto) : "-";
                            })
                            .toArray(String[]::new))
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("generateCsv: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    public byte[] generatePdf(List<AFPBRViewByAccountDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<AFPBRViewByAccountDTO, String>> columnGetters = Arrays.asList(
                    AFPBRViewByAccountDTO::getAcctName,
                    AFPBRViewByAccountDTO::getAcctNumber,
                    AFPBRViewByAccountDTO::getMasterAcctNumber,
                    AFPBRViewByAccountDTO::getAccountCurrency,
                    AFPBRViewByAccountDTO::getFinancedAmount,
                    AFPBRViewByAccountDTO::getSettlementAmount
            );

            float totalTableWidth = calculateTotalTableWidth(datas, columnGetters);
            PageSize pageSize = getOptimalPageSize(totalTableWidth);

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, pageSize);

            float[] columnWidths = calculateColumnWidths(datas, columnGetters, pageSize.getWidth());
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph(headers[0])));
            table.addHeaderCell(new Cell().add(new Paragraph(headers[1])));
            table.addHeaderCell(new Cell().add(new Paragraph(headers[2])));
            table.addHeaderCell(new Cell().add(new Paragraph(headers[3])));
            table.addHeaderCell(new Cell().add(new Paragraph(headers[4])));
            table.addHeaderCell(new Cell().add(new Paragraph(headers[5])));

            datas.forEach(row -> {
                table.addCell(new Cell().add(new Paragraph(row.getAcctName() != null ? row.getAcctName() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getAcctNumber() != null ? row.getAcctNumber() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getMasterAcctNumber() != null ? row.getMasterAcctNumber() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getAccountCurrency() != null ? row.getAccountCurrency() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getFinancedAmount() != null ? row.getFinancedAmount() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getSettlementAmount() != null ? row.getSettlementAmount() : "-")));
            });

            Paragraph title = new Paragraph("Auto Finance PBR View by Account Report")
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

    public byte[] generateTxt(List<AFPBRViewByAccountDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-140s%-20s%-20s%-10s%-20s%-20s\r\n",
                headers[0], headers[1],
                headers[2], headers[3], headers[4], headers[5]));

        datas.forEach(row -> {
            String acctName = row.getAcctName() != null ? row.getAcctName() : "-";
            String acctNumber = row.getAcctNumber() != null ? row.getAcctNumber() : "-";
            String masterAcctNumber = row.getMasterAcctNumber() != null ? row.getMasterAcctNumber() : "-";
            String currency = row.getAccountCurrency() != null ? row.getAccountCurrency() : "-";
            String financedAmt = row.getFinancedAmount() != null ? row.getFinancedAmount() : "-";
            String settlementAmt = row.getSettlementAmount() != null ? row.getSettlementAmount() : "-";

            String formattedRow = String.format("%-140s%-20s%-20s%-10s%-20s%-20s",
                    truncateOrPad(acctName, 140),
                    truncateOrPad(acctNumber, 20),
                    truncateOrPad(masterAcctNumber, 20),
                    truncateOrPad(currency, 10),
                    truncateOrPad(financedAmt, 20),
                    truncateOrPad(settlementAmt, 20));
            sb.append(formattedRow).append("\r\n");
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
