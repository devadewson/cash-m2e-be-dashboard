package com.m2e.dashboard.service.otherbank.loan;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.otherbank.response.loan.OtherBankLoanTrxHistDTO;
import com.m2e.dashboard.enums.ReportFormat;
import com.m2e.dashboard.service.DualParameterFileService;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
public class OtherBankLoanTrxHistFileService implements DualParameterFileService<List<OtherBankLoanTrxHistDTO>> {

    private static final Logger log = LoggerFactory.getLogger(OtherBankLoanTrxHistFileService.class);

    private Map<String, Function<OtherBankLoanTrxHistDTO, String>> getHeaderMap() {
        Map<String, Function<OtherBankLoanTrxHistDTO, String>> headerMap = new HashMap<>();
        headerMap.put("Date", OtherBankLoanTrxHistDTO::getDate);
        headerMap.put("Transaction Reference", OtherBankLoanTrxHistDTO::getTrxRefNo);
        headerMap.put("Transaction Description", OtherBankLoanTrxHistDTO::getTrxDesc);
        headerMap.put("Debit", OtherBankLoanTrxHistDTO::getDebit);
        headerMap.put("Credit", OtherBankLoanTrxHistDTO::getCredit);
        headerMap.put("Entry Date", OtherBankLoanTrxHistDTO::getEntryDate);
        headerMap.put("Transaction Type", OtherBankLoanTrxHistDTO::getTrxType);
        headerMap.put("Transaction Code", OtherBankLoanTrxHistDTO::getTrxCode);
        headerMap.put("Outstanding Balance", OtherBankLoanTrxHistDTO::getOutstandingBalance);
        headerMap.put("Transaction Amount", OtherBankLoanTrxHistDTO::getTrxAmount);
        headerMap.put("Supplementary Details", OtherBankLoanTrxHistDTO::getSupplementaryDetails);
        headerMap.put("Opening Balance", OtherBankLoanTrxHistDTO::getOpeningBalance);
        return headerMap;
    }

    public byte[] generateFile(List<OtherBankLoanTrxHistDTO> data, ReportFormat format, String[] headers) {
        return switch (format) {
            case CSV -> generateCsv(data, headers);
            case PDF -> generatePdf(data, headers);
            case TXT -> generateTxt(data, headers);
            default -> throw new IllegalArgumentException("Report format is undefined");
        };
    }

    @Override
    public byte[] generateCsv(List<OtherBankLoanTrxHistDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            // TODO : modify this logic based on real report format
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> Arrays.stream(headers)
                            .map(header -> {
                                Function<OtherBankLoanTrxHistDTO, String> function = this.getHeaderMap().get(header);
                                if(header.equals("Debit") || header.equals("Credit")) {
                                    return function != null && function.apply(dto) != null ? function.apply(dto) : "0.00";
                                } else {
                                    return function != null && function.apply(dto) != null ? function.apply(dto) : "-";
                                }
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

    @Override
    public byte[] generatePdf(List<OtherBankLoanTrxHistDTO> datas, String[] headers) {
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
                        Function<OtherBankLoanTrxHistDTO, String> function = this.getHeaderMap().get(header);
                        String cellValue;
                        if(header.equals("Debit") || header.equals("Credit")) {
                            cellValue = function != null && function.apply(row) != null ? function.apply(row) : "0.00";
                        } else {
                            cellValue = function != null && function.apply(row) != null ? function.apply(row) : "-";
                        }
                        table.addCell(new Cell().add(new Paragraph(cellValue)));
                    }));
            document.add(new Paragraph("Deposit - CA Transaction History Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("generatePdf: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<OtherBankLoanTrxHistDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); PrintWriter txtWriter = new PrintWriter(writer)) {
            txtWriter.println(String.join("\t", headers));

            datas.stream().forEach(dto -> {
                String[] rowData = Arrays.stream(headers)
                        .map(header -> {
                            Function<OtherBankLoanTrxHistDTO, String> function = this.getHeaderMap().get(header);
                            if(header.equals("Debit") || header.equals("Credit")) {
                                return function != null && function.apply(dto) != null ? function.apply(dto) : "0.00";
                            } else {
                                return function != null && function.apply(dto) != null ? function.apply(dto) : "-";
                            }
                        })
                        .toArray(String[]::new);
                txtWriter.println(String.join("\t", rowData));
            });

            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("generateTxt: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

}
