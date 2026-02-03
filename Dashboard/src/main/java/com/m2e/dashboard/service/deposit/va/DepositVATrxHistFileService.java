package com.m2e.dashboard.service.deposit.va;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.dto.deposit.va.response.DepositVATransHistSearchDTO;
import com.m2e.dashboard.service.DualParameterFileService;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
public class DepositVATrxHistFileService implements DualParameterFileService<List<DepositVATransHistSearchDTO>> {
    public byte[] generateCsv(List<DepositVATransHistSearchDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> Arrays.stream(headers)
                            .map(header -> {
                                Function<DepositVATransHistSearchDTO, String> function = this.getHeaderMap().get(header);
                                return function != null && function.apply(dto)!=null ? function.apply(dto) : "-";
                            })
                            .toArray(String[]::new))
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    public byte[] generatePdf(List<DepositVATransHistSearchDTO> datas, String[] headers) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<DepositVATransHistSearchDTO, String>> columnGetters = Arrays.stream(headers)
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
            datas.stream().forEach(row -> Arrays.stream(headers).forEach(
                    header -> {
                        Function<DepositVATransHistSearchDTO, String> function = this.getHeaderMap().get(header);
                        String cellValue = function != null && function.apply(row) != null ? function.apply(row) : "-";
                        table.addCell(new Cell().add(new Paragraph(cellValue)));
                    }));

            document.add(new Paragraph("Virtual Account Transaction History Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }
    public byte[] generateTxt(List<DepositVATransHistSearchDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); PrintWriter txtWriter = new PrintWriter(writer)) {

            String[] paddedHeaders = Arrays.stream(headers)
                    .map(header -> truncateOrPad(header, 30))
                    .toArray(String[]::new);

            txtWriter.println(String.join("\t", paddedHeaders));

            datas.stream().forEach(dto -> {
                String[] rowData = Arrays.stream(headers)
                        .map(header -> {
                            Function<DepositVATransHistSearchDTO, String> function = this.getHeaderMap().get(header);
                            String value = function != null && function.apply(dto) != null ? function.apply(dto) : "-";
                            return truncateOrPad(value, 30);
                        })
                        .toArray(String[]::new);
                // Write the row data
                txtWriter.println(String.join("\t", rowData));
            });
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private Map<String, Function<DepositVATransHistSearchDTO, String>> getHeaderMap() {
        Map<String, Function<DepositVATransHistSearchDTO, String>> headerMap = new HashMap<>();
        headerMap.put("Date", DepositVATransHistSearchDTO::getDate);
        headerMap.put("Virtual Account No", DepositVATransHistSearchDTO::getVirtualAcctNumber);
        headerMap.put("Virtual Acc Ref No", DepositVATransHistSearchDTO::getVirtualAcctRefNumber);
        headerMap.put("Transaction Description", DepositVATransHistSearchDTO::getTrxDescription);
        headerMap.put("Transaction Code", DepositVATransHistSearchDTO::getTrxCode);
        headerMap.put("Transaction Date", DepositVATransHistSearchDTO::getTransactionDate);
        headerMap.put("Posting Date", DepositVATransHistSearchDTO::getTransactionDate);
        headerMap.put("Transaction Time", DepositVATransHistSearchDTO::getTrxTime);
        headerMap.put("Processing Time", DepositVATransHistSearchDTO::getTrxTime);
        headerMap.put("Currency", DepositVATransHistSearchDTO::getCurrency);
        headerMap.put("Debit", DepositVATransHistSearchDTO::getDebit);
        headerMap.put("Credit", DepositVATransHistSearchDTO::getCredit);
        headerMap.put("Source Code", DepositVATransHistSearchDTO::getSourceCode);
        headerMap.put("Teller ID", DepositVATransHistSearchDTO::getTellerId);

        return headerMap;
    }

    private String truncateOrPad(String input, int length) {
        if (input.length() > length) {
            return input.substring(0, length);
        } else {
            return String.format("%-" + length + "s", input);
        }
    }
}
