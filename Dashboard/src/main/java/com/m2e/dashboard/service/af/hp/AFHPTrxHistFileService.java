package com.m2e.dashboard.service.af.hp;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.af.AFHPHeaders;
import com.m2e.dashboard.enums.ReportFormat;
import com.m2e.dashboard.service.DualParameterFileService;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class AFHPTrxHistFileService<T> implements DualParameterFileService<List<T>> {

    public byte[] generateFile(List<T> data, ReportFormat format,String[] headers ) {
        return switch (format) {
            case CSV -> generateCsv(data, headers);
            case PDF -> generatePdf(data,headers);
            case TXT -> generateTxt(data,headers);
            default -> throw new IllegalArgumentException("Report format is undefined");
        };
    }

    private Map<String, Function<T, String>> getHeaderMap() {
        Map<String, Function<T, String>> headerMap = new HashMap<>();
        headerMap.put(AFHPHeaders.DATE, dto -> invokeGetter(dto, "getTrxDate"));
        headerMap.put(AFHPHeaders.TRANSACTION_REFERENCE, dto -> invokeGetter(dto, "getTrxReference"));
        headerMap.put(AFHPHeaders.TRANSACTION_DESCRIPTION, dto -> invokeGetter(dto, "getTrxDescription"));
        headerMap.put(AFHPHeaders.DEBIT, dto -> invokeGetter(dto, "getDebit"));
        headerMap.put(AFHPHeaders.CREDIT, dto -> invokeGetter(dto, "getCredit"));
        headerMap.put(AFHPHeaders.PROCESSING_TIME, dto -> invokeGetter(dto, "getProcessingTime"));
        headerMap.put(AFHPHeaders.TRANSACTION_DESCRIPTION_2, dto -> invokeGetter(dto, "getTrxDescription2"));
        headerMap.put(AFHPHeaders.TRANSACTION_DESCRIPTION_3, dto -> invokeGetter(dto, "getTrxDescription3"));
        headerMap.put(AFHPHeaders.TRANSACTION_DESCRIPTION_4, dto -> invokeGetter(dto, "getTrxDescription4"));
        headerMap.put(AFHPHeaders.VIRTUAL_ACCOUNT, dto -> invokeGetter(dto, "getVirtualAcct"));
        headerMap.put(AFHPHeaders.TRANSACTION_STATUS, dto -> invokeGetter(dto, "getTrxStatus"));
        headerMap.put(AFHPHeaders.TRANSACTION_TYPE, dto -> invokeGetter(dto, "getTrxType"));
        headerMap.put(AFHPHeaders.SOURCE_CODE, dto -> invokeGetter(dto, "getSrcCode"));
        headerMap.put(AFHPHeaders.TELLER_ID, dto -> invokeGetter(dto, "getTellerId"));
        headerMap.put(AFHPHeaders.OWNERS_INSTITUTION, dto -> invokeGetter(dto, "getOwnerInstitution"));
        headerMap.put(AFHPHeaders.TRANSACTION_CODE, dto -> invokeGetter(dto, "getTrxCode"));
        headerMap.put(AFHPHeaders.AUXILIARY_TRANSACTION_CODE, dto -> invokeGetter(dto, "getAuxiliaryTrxCode"));
        headerMap.put(AFHPHeaders.EXPIRY_DATE, dto -> invokeGetter(dto, "getExpiryDate"));
        return headerMap;
    }


    private String invokeGetter(Object obj, String methodName) {
        try {
            Method method = obj.getClass().getMethod(methodName);
            Object value = method.invoke(obj);
            return value != null ? value.toString() : "-";
        } catch (Exception e) {
            log.error("Error invoking method: " + methodName, e);
            return "-";
        }
    }

    private String[] getDefaultHeaders() {
        return new String[]{
                AFHPHeaders.DATE,
                AFHPHeaders.TRANSACTION_REFERENCE,
                AFHPHeaders.TRANSACTION_DESCRIPTION,
                AFHPHeaders.DEBIT,
                AFHPHeaders.CREDIT
        };
    }

    public byte[] generateCsv(List<T> datas, String[] headers) {
        if (headers == null || headers.length == 0) {
            headers = getDefaultHeaders();
        }
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(headers);
            String[] finalHeaders = headers;
            List<String[]> dataRows = datas.stream()
                    .map(dto -> Arrays.stream(finalHeaders)
                            .map(header -> {
                                Function<T, String> function = this.getHeaderMap().get(header);
                                return function != null ? function.apply(dto) : "-";
                            })
                            .toArray(String[]::new))
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error generating CSV file", e);
            return new byte[0];
        }
    }

    public byte[] generatePdf(List<T> datas, String[] headers) {
        if (headers == null || headers.length == 0) {
            headers = getDefaultHeaders();
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Table table = new Table(headers.length);
            Arrays.stream(headers).forEach(header -> table.addHeaderCell(new Cell().add(new Paragraph(header))));
            String[] finalHeaders = headers;
            datas.forEach(row -> Arrays.stream(finalHeaders).forEach(header -> {
                Function<T, String> function = this.getHeaderMap().get(header);
                String cellValue = function != null ? function.apply(row) : "-";
                table.addCell(new Cell().add(new Paragraph(cellValue)));
            }));
            document.add(new Paragraph("Hire Purchase Transaction History Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("Error generating PDF file", e);
            return new byte[0];
        }
    }

    public byte[] generateTxt(List<T> datas, String[] headers) {
        if (headers == null || headers.length == 0) {
            headers = getDefaultHeaders();
        }
        try (StringWriter writer = new StringWriter(); PrintWriter txtWriter = new PrintWriter(writer)) {
            txtWriter.println(String.join("\t", headers));

            String[] finalHeaders = headers;
            datas.forEach(dto -> {
                String[] rowData = Arrays.stream(finalHeaders)
                        .map(header -> {
                            Function<T, String> function = this.getHeaderMap().get(header);
                            return function != null ? function.apply(dto) : "-";
                        })
                        .toArray(String[]::new);
                txtWriter.println(String.join("\t", rowData));
            });

            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Error generating TXT file", e);
            return new byte[0];
        }
    }
}


