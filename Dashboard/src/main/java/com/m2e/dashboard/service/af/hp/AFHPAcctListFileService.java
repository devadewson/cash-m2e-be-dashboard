package com.m2e.dashboard.service.af.hp;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.dto.af.response.hp.AFHPAcctListDTO;
import com.m2e.dashboard.enums.ReportFormat;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
@Slf4j
public class AFHPAcctListFileService implements FileService<List<AFHPAcctListDTO>> {

    public byte[] generateFile(List<AFHPAcctListDTO> data, ReportFormat format) {
        return switch (format) {
            case CSV -> generateCsv(data);
            case PDF -> generatePdf(data);
            case TXT -> generateTxt(data);
            default -> throw new IllegalArgumentException("Report format is undefined");
        };
    }

    @Override
    public byte[] generateCsv(List<AFHPAcctListDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            // TODO : modify this logic based on real report format
            String[] headers = {"Account Name", "Account Number", "Master Account Number", "Financed Amount", "Outstanding Balance"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getAcctName() != null ? dto.getAcctName() : "",
                            dto.getAcctNumber() != null && dto.getCurrency() != null ? dto.getAcctNumber().concat(" - ").concat(dto.getCurrency()) : "",
                            dto.getMasterAcctNumber() != null ? dto.getMasterAcctNumber() : "",
                            dto.getFinancedAmt() != null ? dto.getFinancedAmt() : "",
                            dto.getOutstandingBalance() != null ? dto.getOutstandingBalance() : ""
                    })
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error occurred while generating CSV: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<AFHPAcctListDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<AFHPAcctListDTO, String>> columnGetters = Arrays.asList(
                    AFHPAcctListDTO::getAcctName,
                    AFHPAcctListDTO::getAcctNumber,
                    AFHPAcctListDTO::getMasterAcctNumber,
                    AFHPAcctListDTO::getFinancedAmt,
                    AFHPAcctListDTO::getOutstandingBalance
            );
            float totalTableWidth = calculateTotalTableWidth(datas, columnGetters);
            PageSize pageSize = getOptimalPageSize(totalTableWidth);

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, pageSize);

            float[] columnWidths = calculateColumnWidths(datas, columnGetters, pageSize.getWidth());
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Account Name").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Account Number").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Master Account Number").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Financed Amount").setBold()));
            table.addHeaderCell(new Cell().add(new Paragraph("Outstanding Balance").setBold()));

            datas.forEach(row -> {
                table.addCell(new Cell().add(new Paragraph(row.getAcctName() != null ? row.getAcctName() : "")));
                table.addCell(new Cell().add(new Paragraph((row.getAcctNumber() != null && row.getCurrency() != null) ? row.getAcctNumber().concat(" - ").concat(row.getCurrency()) : "")));
                table.addCell(new Cell().add(new Paragraph(row.getMasterAcctNumber() != null ? row.getMasterAcctNumber() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getFinancedAmt() != null ? row.getFinancedAmt() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getOutstandingBalance() != null ? row.getOutstandingBalance() : "")));
            });

            Paragraph title = new Paragraph("Auto Finance - Hire Purchase Account Summary List")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold();
            document.add(title);
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error occurred while generating PDF: {}", e.getMessage(), e);
            return new byte[0];
        }
    }

    private Map<String, Function<AFHPAcctListDTO, String>> getHeaderMap() {
        Map<String, Function<AFHPAcctListDTO, String>> headerMap = new HashMap<>();
        headerMap.put("Account Name", AFHPAcctListDTO::getAcctName);
        headerMap.put("Account Number", AFHPAcctListDTO::getAcctNumber);
        headerMap.put("Master Account Number", AFHPAcctListDTO::getMasterAcctNumber);
        headerMap.put("Financed Amount", AFHPAcctListDTO::getFinancedAmt);
        headerMap.put("Outstanding Balance", AFHPAcctListDTO::getOutstandingBalance);
        return headerMap;
    }
    
    @Override
    public byte[] generateTxt(List<AFHPAcctListDTO> datas) {
        Map<String, Function<AFHPAcctListDTO, String>> mapHeaders = getHeaderMap();
        String[] headers = {"Account Name", "Account Number", "Master Account Number", "Financed Amount", "Outstanding Balance"};
        List<String> selectedHeaders = Arrays.asList(headers);

        //compute column width
        Map<String, Integer> columnWidths = selectedHeaders.stream()
                .filter(mapHeaders::containsKey)
                .collect(Collectors.toMap(h -> h,
                        h -> {
                            Function<AFHPAcctListDTO, String> value = mapHeaders.get(h);
                            return Math.max(h.length(),
                                    datas.stream()
                                            .map(item -> Optional.ofNullable(value.apply(item)).orElse("-"))
                                            .mapToInt(String::length)
                                            .max()
                                            .orElse(0)
                            );
                        },
                        (a, b) -> b,
                        HashMap::new));

        try (StringWriter sw = new StringWriter(); PrintWriter pw = new PrintWriter(sw)) {
            //print headers
            selectedHeaders.stream()
                    .filter(columnWidths::containsKey)
                    .forEach(header -> pw.print(padRight(header, columnWidths.get(header)) + "\t"));

            pw.println();

            //print rows
            datas.forEach(item -> {
                selectedHeaders.stream()
                        .filter(columnWidths::containsKey)
                        .forEach(header -> {
                            String val = Optional.ofNullable(mapHeaders.get(header).apply(item)).orElse("-");
                            pw.print(padRight(val, columnWidths.get(header)) + "\t");
                        });
                pw.println();
            });

            return sw.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("generateTxt: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    private String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }
}
