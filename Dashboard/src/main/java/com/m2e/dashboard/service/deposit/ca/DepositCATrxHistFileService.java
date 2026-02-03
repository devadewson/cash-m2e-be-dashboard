package com.m2e.dashboard.service.deposit.ca;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.dto.deposit.ca.response.DepositTrxHistoryDTO;
import com.m2e.dashboard.service.DualParameterFileService;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
@Slf4j
public class DepositCATrxHistFileService implements DualParameterFileService<List<DepositTrxHistoryDTO>> {


    private Map<String, Function<DepositTrxHistoryDTO, String>> getHeaderMap() {
        Map<String, Function<DepositTrxHistoryDTO, String>> headerMap = new HashMap<>();
        headerMap.put("Date", DepositTrxHistoryDTO::getDate);
        headerMap.put("Processing Time", DepositTrxHistoryDTO::getProcessingTime);
        headerMap.put("Transaction Reference", DepositTrxHistoryDTO::getTrxReference);
        headerMap.put("Transaction Description", DepositTrxHistoryDTO::getTrxDesc);
        headerMap.put("Transaction Description 2", DepositTrxHistoryDTO::getTrxDesc2);
        headerMap.put("Transaction Description 3", DepositTrxHistoryDTO::getTrxDesc3);
        headerMap.put("Transaction Description 4", DepositTrxHistoryDTO::getTrxDesc4);
        headerMap.put("Transaction Description 5", DepositTrxHistoryDTO::getTrxDesc5);
        headerMap.put("Transaction Description 6", DepositTrxHistoryDTO::getTrxDesc6);
        headerMap.put("Currency", DepositTrxHistoryDTO::getCurrency);
        headerMap.put("Debit", DepositTrxHistoryDTO::getDebit);
        headerMap.put("Credit", DepositTrxHistoryDTO::getCredit);
        headerMap.put("Source Code", DepositTrxHistoryDTO::getSourceCode);
        headerMap.put("Teller ID", DepositTrxHistoryDTO::getTellerId);
        headerMap.put("Branch/Channel Code", DepositTrxHistoryDTO::getBranch);
        headerMap.put("Transaction Code", DepositTrxHistoryDTO::getTrxCode);
        headerMap.put("Auxiliary Transaction Code", DepositTrxHistoryDTO::getAuxTrxCode);
        headerMap.put("Expiry Date", DepositTrxHistoryDTO::getExpiryDate);
        headerMap.put("Opening Balance", DepositTrxHistoryDTO::getBeginBalance);
        headerMap.put("Closing Balance", DepositTrxHistoryDTO::getEndBalance);
        headerMap.put("Virtual Account", DepositTrxHistoryDTO::getVirtualAcct);
        return headerMap;
    }

    public byte[] generateCsv(List<DepositTrxHistoryDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            // TODO : modify this logic based on real report format
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> Arrays.stream(headers)
                            .map(header -> {
                                Function<DepositTrxHistoryDTO, String> function = this.getHeaderMap().get(header);
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

    public byte[] generatePdf(List<DepositTrxHistoryDTO> datas, String[] headers) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<DepositTrxHistoryDTO, String>> columnGetters = Arrays.stream(headers)
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
            float unit = table.getWidth().getValue();

            Arrays.stream(headers)
                    .forEach(header -> table.addHeaderCell(new Cell().add(new Paragraph(header))));
            datas.stream().forEach(row -> Arrays.stream(headers).forEach(
                    header -> {
                        Function<DepositTrxHistoryDTO, String> function = this.getHeaderMap().get(header);
                        String cellValue = function != null && function.apply(row) != null ? function.apply(row) : "-";
                        table.addCell(new Cell().add(new Paragraph(cellValue)));
                    }));
            document.add(new Paragraph("Deposit - CA Transaction History Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public byte[] generateTxt(List<DepositTrxHistoryDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); PrintWriter txtWriter = new PrintWriter(writer)) {
            //compute max width for each column
            int[] maxWidths = new int[headers.length];

            //initialize using header lengths
            for (int i = 0; i < headers.length; i++) {
                maxWidths[i] = headers[i].length();
            }

            //compute row value lengths
            for (DepositTrxHistoryDTO dto : datas) {
                for (int i = 0; i < headers.length; i++) {
                    Function<DepositTrxHistoryDTO, String> function = this.getHeaderMap().get(headers[i]);
                    String value = (function != null ? function.apply(dto) : "-");
                    value = (value != null ? value : "-");

                    maxWidths[i] = Math.max(maxWidths[i], value.length());
                }
            }

            //output header row
            for (int i = 0; i < headers.length; i++) {
                txtWriter.print(padRight(headers[i], maxWidths[i] + 2)); //added 2 spaces between header row values
            }
            txtWriter.println();

            //output each row
            for (DepositTrxHistoryDTO dto : datas) {
                for (int i = 0; i < headers.length; i++) {
                    Function<DepositTrxHistoryDTO, String> function = this.getHeaderMap().get(headers[i]);
                    String value = (function != null ? function.apply(dto) : "-");
                    value = (value != null ? value : "-");

                    txtWriter.print(padRight(value, maxWidths[i] + 2));
                }
                txtWriter.println();
            }

            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    private String padRight(String text, int width) {
        return String.format("%-" + width + "s", text);
    }

}
