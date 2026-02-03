package com.m2e.dashboard.service.af.bd;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.af.response.bd.AFBDSubMasterAcctListDTO;
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

@Service
@Slf4j
public class AFBDSubAcctMasterListFileService implements FileService<List<AFBDSubMasterAcctListDTO>> {

    private static final String[] headers = new String[]{"Account Name", "Account Number", "Debit Note", "Financed Amount", "Outstanding Balance"};

    private static final Map<String, Function<AFBDSubMasterAcctListDTO, String>> HEADER_MAP;
    static {
        Map<String, Function<AFBDSubMasterAcctListDTO, String>> headerMap = new HashMap<>();
        headerMap.put(headers[0], AFBDSubMasterAcctListDTO::getAcctName);
        headerMap.put(headers[1], AFBDSubMasterAcctListDTO::getAcctNumber);
        headerMap.put(headers[2], AFBDSubMasterAcctListDTO::getDebitNote);
        headerMap.put(headers[3], AFBDSubMasterAcctListDTO::getFinancedAmt);
        headerMap.put(headers[4], AFBDSubMasterAcctListDTO::getOutstandingBalance);
        HEADER_MAP = Collections.unmodifiableMap(headerMap);
    }

    private Map<String, Function<AFBDSubMasterAcctListDTO, String>> getHeaderMap() {
        return HEADER_MAP;
    }

    public byte[] generateFile(List<AFBDSubMasterAcctListDTO> data, ReportFormat format) {
        return switch (format) {
            case CSV -> generateCsv(data);
            case PDF -> generatePdf(data);
            case TXT -> generateTxt(data);
            default -> throw new IllegalArgumentException("Report format is undefined");
        };
    }

    @Override
    public byte[] generateCsv(List<AFBDSubMasterAcctListDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> Arrays.stream(headers)
                            .map(header -> {
                                Function<AFBDSubMasterAcctListDTO, String> function = this.getHeaderMap().get(header);
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

    @Override
    public byte[] generatePdf(List<AFBDSubMasterAcctListDTO> datas) {
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
                        Function<AFBDSubMasterAcctListDTO, String> function = this.getHeaderMap().get(header);
                        String cellValue = function != null && function.apply(row) != null ? function.apply(row) : "-";
                        table.addCell(new Cell().add(new Paragraph(cellValue)));
                    }));

            document.add(new Paragraph("Autofinance - BD Sub Master List Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("generatePdf: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<AFBDSubMasterAcctListDTO> datas) {
        Map<String, Function<AFBDSubMasterAcctListDTO, String>> mapHeaders = getHeaderMap();
        List<String> selectedHeaders = Arrays.asList(headers);

        //compute column width
        Map<String, Integer> columnWidths = selectedHeaders.stream()
                .filter(mapHeaders::containsKey)
                .collect(Collectors.toMap(h -> h,
                        h -> {
                            Function<AFBDSubMasterAcctListDTO, String> value = mapHeaders.get(h);
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
