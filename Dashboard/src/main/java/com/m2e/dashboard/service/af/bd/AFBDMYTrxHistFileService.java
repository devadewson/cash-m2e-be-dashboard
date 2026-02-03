package com.m2e.dashboard.service.af.bd;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.af.response.bd.AFBDSubMasterAcctListDTO;
import com.m2e.dashboard.dto.af.response.bd.AFBDTrxHistoryDTO;
import com.m2e.dashboard.enums.ReportFormat;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class AFBDMYTrxHistFileService {

    private Map<String, Function<AFBDTrxHistoryDTO, String>> getHeaderMap() {
        Map<String, Function<AFBDTrxHistoryDTO, String>> headerMap = new HashMap<>();
        headerMap.put("Date", AFBDTrxHistoryDTO::getTrxDate);
        headerMap.put("Transaction Reference", AFBDTrxHistoryDTO::getTrxReference);
        headerMap.put("Transaction Description", AFBDTrxHistoryDTO::getTrxDesc);
        headerMap.put("Debit", AFBDTrxHistoryDTO::getDebit);
        headerMap.put("Credit", AFBDTrxHistoryDTO::getCredit);
        headerMap.put("Processing Time", AFBDTrxHistoryDTO::getTrxDate);
        headerMap.put("Transaction Description 2", AFBDTrxHistoryDTO::getTrxDesc2);
        headerMap.put("Transaction Description 3", AFBDTrxHistoryDTO::getTrxDesc3);
        headerMap.put("Transaction Description 4", AFBDTrxHistoryDTO::getTrxDesc4);
        headerMap.put("Virtual Account", AFBDTrxHistoryDTO::getVirtualAcct);
        headerMap.put("Transaction Status", AFBDTrxHistoryDTO::getTrxStatus);
        headerMap.put("Transaction Type", AFBDTrxHistoryDTO::getTrxType);
        headerMap.put("Source Code", AFBDTrxHistoryDTO::getSrcCode);
        headerMap.put("Teller ID", AFBDTrxHistoryDTO::getTellerId);
        headerMap.put("Owner's Institution", AFBDTrxHistoryDTO::getOwnerInstitution);
        headerMap.put("Transaction Code", AFBDTrxHistoryDTO::getTrxCode);
        headerMap.put("Auxiliary Transaction Code", AFBDTrxHistoryDTO::getAuxiliaryTrxCode);
        headerMap.put("Expiry Date/Effective Date", AFBDTrxHistoryDTO::getExpiryDate);
        return headerMap;
    }

    public byte[] generateFile(List<AFBDTrxHistoryDTO> data, ReportFormat format, String[] headers) {
        return switch (format) {
            case CSV -> generateCsv(data, headers);
            case PDF -> generatePdf(data, headers);
            case TXT -> generateTxt(data, headers);
            default -> throw new IllegalArgumentException("Report format is undefined");
        };
    }

    public byte[] generateCsv(List<AFBDTrxHistoryDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> Arrays.stream(headers)
                            .map(header -> {
                                Function<AFBDTrxHistoryDTO, String> function = this.getHeaderMap().get(header);
                                return function != null && function.apply(dto) != null ? function.apply(dto) : "-";
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

    public byte[] generatePdf(List<AFBDTrxHistoryDTO> datas, String[] headers) {
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
                        Function<AFBDTrxHistoryDTO, String> function = this.getHeaderMap().get(header);
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

    public byte[] generateTxt(List<AFBDTrxHistoryDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); PrintWriter txtWriter = new PrintWriter(writer)) {
            txtWriter.println(String.join("\t", headers));

            datas.stream().forEach(dto -> {
                String[] rowData = Arrays.stream(headers)
                        .map(header -> {
                            Function<AFBDTrxHistoryDTO, String> function = this.getHeaderMap().get(header);
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
