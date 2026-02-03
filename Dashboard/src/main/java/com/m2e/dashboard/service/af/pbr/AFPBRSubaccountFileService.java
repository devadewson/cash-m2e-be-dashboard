package com.m2e.dashboard.service.af.pbr;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRUnitSummaryDTO;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

@Service
public class AFPBRSubaccountFileService implements FileService<List<AFPBRUnitSummaryDTO>> {

    private static final String[] headers = {"Account Name", "Account Number", "Financed Amount", "Settlement Amount"};

    private static final Map<String, Function<AFPBRUnitSummaryDTO, String>> HEADER_MAP;
    static {
        Map<String, Function<AFPBRUnitSummaryDTO, String>> headerMap = new HashMap<>();
        headerMap.put(headers[0], AFPBRUnitSummaryDTO::getAcctName);
        headerMap.put(headers[1], AFPBRUnitSummaryDTO::getUnitNumber);
        headerMap.put(headers[2], AFPBRUnitSummaryDTO::getFinancedAmount);
        headerMap.put(headers[3], AFPBRUnitSummaryDTO::getSettlementAmount);
        HEADER_MAP = Collections.unmodifiableMap(headerMap);
    }

    private Map<String, Function<AFPBRUnitSummaryDTO, String>> getHeaderMap() {
        return HEADER_MAP;
    }

    @Override
    public byte[] generateCsv(List<AFPBRUnitSummaryDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            // TODO : modify this logic based on real report format
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> Arrays.stream(headers)
                            .map(header -> {
                                Function<AFPBRUnitSummaryDTO, String> function = this.getHeaderMap().get(header);
                                return function != null && function.apply(dto)!=null ? function.apply(dto) : "-";
                            })
                            .toArray(String[]::new))
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<AFPBRUnitSummaryDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            Table table = new Table(headers.length);

            Arrays.stream(headers)
                    .forEach(header -> table.addHeaderCell(new Cell().add(new Paragraph(header))));

            datas.stream().forEach(row -> Arrays.stream(headers).forEach(
                    header -> {
                        Function<AFPBRUnitSummaryDTO, String> function = this.getHeaderMap().get(header);
                        String cellValue = function != null && function.apply(row) != null ? function.apply(row) : "-";
                        table.addCell(new Cell().add(new Paragraph(cellValue)));
                    }));

            document.add(new Paragraph("Autofinance - PBR Subaccount Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<AFPBRUnitSummaryDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-140s%-20s%-20s%-20s\r\n",
                headers[0], headers[1],
                headers[2], headers[3]));

        datas.forEach(row -> {
            String acctName = row.getAcctName() != null ? row.getAcctName() : "-";
            String acctNumber = row.getUnitNumber() != null && row.getAccountCurrency() != null ? row.getUnitNumber().concat(" - ").concat(row.getAccountCurrency()) : "-";
            String financedAmt = row.getFinancedAmount() != null ? row.getFinancedAmount() : "-";
            String settlementAmt = row.getSettlementAmount() != null ? row.getSettlementAmount() : "-";

            String formattedRow = String.format("%-140s%-20s%-20s%-20s",
                    truncateOrPad(acctName, 140),
                    truncateOrPad(acctNumber, 20),
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
