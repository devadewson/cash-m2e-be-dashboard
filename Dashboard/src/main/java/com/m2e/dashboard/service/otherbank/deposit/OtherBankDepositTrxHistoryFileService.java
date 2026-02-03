package com.m2e.dashboard.service.otherbank.deposit;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.otherbank.response.deposit.OtherBankDepositTrxHistoryDTO;
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
public class OtherBankDepositTrxHistoryFileService implements DualParameterFileService<List<OtherBankDepositTrxHistoryDTO>> {

    private static final Logger log = LoggerFactory.getLogger(OtherBankDepositTrxHistoryFileService.class);

    private Map<String, Function<OtherBankDepositTrxHistoryDTO, String>> getHeaderMap() {
        Map<String, Function<OtherBankDepositTrxHistoryDTO, String>> headerMap = new HashMap<>();
        headerMap.put("Date", OtherBankDepositTrxHistoryDTO::getDate);
        headerMap.put("Transaction Reference", OtherBankDepositTrxHistoryDTO::getTrxRefNo);
        headerMap.put("Transaction Description", OtherBankDepositTrxHistoryDTO::getTrxDesc);
        headerMap.put("Debit", OtherBankDepositTrxHistoryDTO::getDebit);
        headerMap.put("Credit", OtherBankDepositTrxHistoryDTO::getCredit);
        headerMap.put("Entry Date", OtherBankDepositTrxHistoryDTO::getEntryDate);
        headerMap.put("Transaction Type", OtherBankDepositTrxHistoryDTO::getTrxType);
        headerMap.put("Transaction Code", OtherBankDepositTrxHistoryDTO::getTrxCode);
        headerMap.put("Closing Balance", OtherBankDepositTrxHistoryDTO::getClosingBalance);
        headerMap.put("Transaction Amount", OtherBankDepositTrxHistoryDTO::getTrxAmount);
        headerMap.put("Supplementary Details", OtherBankDepositTrxHistoryDTO::getSupplementaryDetails);
        headerMap.put("Opening Balance", OtherBankDepositTrxHistoryDTO::getOpeningBalance);
        headerMap.put("Swift Transaction Code", OtherBankDepositTrxHistoryDTO::getOpeningBalance);
        return headerMap;
    }

    @Override
    public byte[] generateCsv(List<OtherBankDepositTrxHistoryDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            // TODO : modify this logic based on real report format
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> Arrays.stream(headers)
                            .map(header -> {
                                Function<OtherBankDepositTrxHistoryDTO, String> function = this.getHeaderMap().get(header);
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
    public byte[] generatePdf(List<OtherBankDepositTrxHistoryDTO> datas, String[] headers) {
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
                        Function<OtherBankDepositTrxHistoryDTO, String> function = this.getHeaderMap().get(header);
                        String cellValue;
                        if(header.equals("Debit") || header.equals("Credit")) {
                            cellValue = function != null && function.apply(row) != null ? function.apply(row) : "0.00";
                        } else {
                            cellValue = function != null && function.apply(row) != null ? function.apply(row) : "-";
                        }
                        table.addCell(new Cell().add(new Paragraph(cellValue)));
                    }));
            document.add(new Paragraph("Other Bank Deposit Transaction History Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("generatePdf: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<OtherBankDepositTrxHistoryDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); PrintWriter txtWriter = new PrintWriter(writer)) {
            txtWriter.println(String.join("\t", headers));

            datas.stream().forEach(dto -> {
                String[] rowData = Arrays.stream(headers)
                        .map(header -> {
                            Function<OtherBankDepositTrxHistoryDTO, String> function = this.getHeaderMap().get(header);
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
