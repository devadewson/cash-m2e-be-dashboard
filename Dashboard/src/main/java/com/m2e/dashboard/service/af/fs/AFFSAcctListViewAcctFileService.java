package com.m2e.dashboard.service.af.fs;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.service.FileService;
import com.m2e.dashboard.sp.af.fs.response.AFFSCertListSpDTO;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AFFSAcctListViewAcctFileService implements FileService<List<AFFSCertListSpDTO>> {
    private static final Logger logger = LoggerFactory.getLogger(AFFSAcctListViewAcctFileService.class);

    private static final String[] headers = {"Account Name", "Account Number", "Master Account No", "Currency", "Financed Amount", "Outstanding Balance"};

    private static final Map<String, Function<AFFSCertListSpDTO, String>> HEADER_MAP;
    static {
        Map<String, Function<AFFSCertListSpDTO, String>> headerMap = new HashMap<>();
        headerMap.put(headers[0], AFFSCertListSpDTO::getMasterAcctName);
        headerMap.put(headers[1], AFFSCertListSpDTO::getMasterAcctNumber);
        headerMap.put(headers[2], AFFSCertListSpDTO::getMasterAcctNumber);
        headerMap.put(headers[3], AFFSCertListSpDTO::getCurrency);
        headerMap.put(headers[4], AFFSCertListSpDTO::getFinancedAmount);
        headerMap.put(headers[5], AFFSCertListSpDTO::getOutstandingBalance);
        HEADER_MAP = Collections.unmodifiableMap(headerMap);
    }

    private Map<String, Function<AFFSCertListSpDTO, String>> getHeaderMap() {
        return HEADER_MAP;
    }

    @Override
    public byte[] generateCsv(List<AFFSCertListSpDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> {
                        return new String[] {
                                dto.getMasterAcctName() != null ? dto.getMasterAcctName() : "-",
                                dto.getCertNo() != null ? dto.getCertNo() : "-",
                                dto.getMasterAcctNumber() != null ? dto.getMasterAcctNumber()  : "-",
                                dto.getCurrency() != null ? dto.getCurrency()  : "-",
                                dto.getFinancedAmount() != null ? dto.getFinancedAmount() : "-",
                                dto.getOutstandingBalance() != null ? dto.getOutstandingBalance() : "-"
                        };
                    })
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("generateCsv: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<AFFSCertListSpDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<AFFSCertListSpDTO, String>> columnGetters = Arrays.asList(
                    AFFSCertListSpDTO::getMasterAcctName,
                    AFFSCertListSpDTO::getCertNo,
                    AFFSCertListSpDTO::getMasterAcctNumber,
                    AFFSCertListSpDTO::getCurrency,
                    AFFSCertListSpDTO::getFinancedAmount,
                    AFFSCertListSpDTO::getOutstandingBalance
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
                table.addCell(new Cell().add(new Paragraph(row.getMasterAcctName() != null ? row.getMasterAcctName() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getCertNo() != null ? row.getCertNo() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getMasterAcctNumber() != null ? row.getMasterAcctNumber() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getCurrency() != null ? row.getCurrency() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getFinancedAmount() != null ? row.getFinancedAmount() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getOutstandingBalance() != null ? row.getOutstandingBalance() : "-")));
            });

            Paragraph title = new Paragraph("Auto Finance Floor Stock Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold();
            document.add(title);
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("generatePdf: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<AFFSCertListSpDTO> datas) {
        Map<String, Function<AFFSCertListSpDTO, String>> mapHeaders = getHeaderMap();
        List<String> selectedHeaders = Arrays.asList(headers);

        //compute column width
        Map<String, Integer> columnWidths = selectedHeaders.stream()
                .filter(mapHeaders::containsKey)
                .collect(Collectors.toMap(h -> h,
                        h -> {
                            Function<AFFSCertListSpDTO, String> value = mapHeaders.get(h);
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
