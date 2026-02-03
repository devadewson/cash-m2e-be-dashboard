package com.m2e.dashboard.service.af.pbr;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRTrxHistoryDTO;
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
public class AFPBRTrxHistoryFileService {

    private static final Logger log = LoggerFactory.getLogger(AFPBRTrxHistoryFileService.class);
    private Map<String, Function<AFPBRTrxHistoryDTO, String>> getHeaderMap() {
        Map<String, Function<AFPBRTrxHistoryDTO, String>> headerMap = new HashMap<>();
        headerMap.put("Date", AFPBRTrxHistoryDTO::getTrxDate);
        headerMap.put("Transaction Reference", AFPBRTrxHistoryDTO::getTrxReference);
        headerMap.put("Transaction Description", AFPBRTrxHistoryDTO::getTrxDesc);
        headerMap.put("Debit", AFPBRTrxHistoryDTO::getDebit);
        headerMap.put("Credit", AFPBRTrxHistoryDTO::getCredit);
        headerMap.put("Processing Time", AFPBRTrxHistoryDTO::getTrxDate);
        headerMap.put("Transaction Description 2", AFPBRTrxHistoryDTO::getTrxDesc2);
        headerMap.put("Transaction Description 3", AFPBRTrxHistoryDTO::getTrxDesc3);
        headerMap.put("Transaction Description 4", AFPBRTrxHistoryDTO::getTrxDesc4);
        headerMap.put("Virtual Account", AFPBRTrxHistoryDTO::getVirtualAcct);
        headerMap.put("Transaction Status", AFPBRTrxHistoryDTO::getTrxStatus);
        headerMap.put("Transaction Type", AFPBRTrxHistoryDTO::getTrxType);
        headerMap.put("Source Code", AFPBRTrxHistoryDTO::getSrcCode);
        headerMap.put("Teller ID", AFPBRTrxHistoryDTO::getTellerId);
        headerMap.put("Owner's Institution", AFPBRTrxHistoryDTO::getOwnerInstitution);
        headerMap.put("Transaction Code", AFPBRTrxHistoryDTO::getTrxCode);
        headerMap.put("Auxiliary Transaction Code", AFPBRTrxHistoryDTO::getAuxiliaryTrxCode);
        headerMap.put("Expiry Date/Effective Date", AFPBRTrxHistoryDTO::getExpiryDate);
        return headerMap;
    }

    public byte[] generateCsv(List<AFPBRTrxHistoryDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            // TODO : modify this logic based on real report format
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> Arrays.stream(headers)
                        .map(header -> {
                            Function<AFPBRTrxHistoryDTO, String> function = this.getHeaderMap().get(header);
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

    public byte[] generatePdf(List<AFPBRTrxHistoryDTO> datas, String[] headers) {
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
                    Function<AFPBRTrxHistoryDTO, String> function = this.getHeaderMap().get(header);
                    String cellValue = function != null && function.apply(row) != null ? function.apply(row) : "-";
                    table.addCell(new Cell().add(new Paragraph(cellValue)));
                }));

            document.add(new Paragraph("Autofinance - PBR Transaction History Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("generatePdf: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    public byte[] generateTxt(List<AFPBRTrxHistoryDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); PrintWriter txtWriter = new PrintWriter(writer)) {
            txtWriter.println(String.join("\t", headers));

            datas.stream().forEach(dto -> {
                String[] rowData = Arrays.stream(headers)
                        .map(header -> {
                            Function<AFPBRTrxHistoryDTO, String> function = this.getHeaderMap().get(header);
                            return function != null && function.apply(dto) != null ? function.apply(dto) : "-";
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
