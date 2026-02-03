package com.m2e.dashboard.service.deposit.fd;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.dto.deposit.fd.response.DepositFDTrxHistoryDTO;
import com.m2e.dashboard.service.DualParameterFileService;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
public class DepositFDTrxHistoryFileService implements DualParameterFileService<List<DepositFDTrxHistoryDTO>> {

    private Map<String, Function<DepositFDTrxHistoryDTO, String>> getHeaderMap() {
        Map<String, Function<DepositFDTrxHistoryDTO, String>> headerMap = new HashMap<>();
        headerMap.put("Date", dto -> convertTo12HourFormat(dto.getDate()));
        headerMap.put("Transaction Reference", DepositFDTrxHistoryDTO::getTrxReference);
        headerMap.put("Transaction Description", DepositFDTrxHistoryDTO::getTrxDesc);
        headerMap.put("Debit", dto -> formatCurrencyAmount(dto.getCurrency(), dto.getDebit(), "D"));
        headerMap.put("Credit", dto -> formatCurrencyAmount(dto.getCurrency(), dto.getCredit(), "C"));
        headerMap.put("Processing Time", DepositFDTrxHistoryDTO::getProcessingTime);
        headerMap.put("Transaction Description 2", DepositFDTrxHistoryDTO::getTrxDesc2);
        headerMap.put("Expiry Date", DepositFDTrxHistoryDTO::getExpiryDate);
        headerMap.put("Currency", DepositFDTrxHistoryDTO::getCurrency);
        headerMap.put("Source Code", DepositFDTrxHistoryDTO::getSourceCode);
        headerMap.put("Teller ID", DepositFDTrxHistoryDTO::getTellerId);
        headerMap.put("Branch/Channel Code", DepositFDTrxHistoryDTO::getBranch);
        headerMap.put("Transaction Code", DepositFDTrxHistoryDTO::getTrxCode);
        headerMap.put("Opening Balance", DepositFDTrxHistoryDTO::getBeginningBalance);
        headerMap.put("Closing Balance", DepositFDTrxHistoryDTO::getEndBalance);
        return headerMap;
    }

    public byte[] generateCsv(List<DepositFDTrxHistoryDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            // TODO : modify this logic based on real report format
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> Arrays.stream(headers)
                            .map(header -> {
                                Function<DepositFDTrxHistoryDTO, String> function = this.getHeaderMap().get(header);
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

    public byte[] generatePdf(List<DepositFDTrxHistoryDTO> datas, String[] headers) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<DepositFDTrxHistoryDTO, String>> columnGetters = Arrays.stream(headers)
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
                        Function<DepositFDTrxHistoryDTO, String> function = this.getHeaderMap().get(header);
                        String cellValue = function != null && function.apply(row) != null ? function.apply(row) : "-";
                        table.addCell(new Cell().add(new Paragraph(cellValue)));
                    }));
            document.add(new Paragraph("Deposit - FD Transaction History Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    public byte[] generateTxt(List<DepositFDTrxHistoryDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); PrintWriter txtWriter = new PrintWriter(writer)) {
            //compute max width for each column
            int[] maxWidths = new int[headers.length];

            //initialize using header lengths
            for (int i = 0; i < headers.length; i++) {
                maxWidths[i] = headers[i].length();
            }

            //compute row value lengths
            for (DepositFDTrxHistoryDTO dto : datas) {
                for (int i = 0; i < headers.length; i++) {
                    Function<DepositFDTrxHistoryDTO, String> function = this.getHeaderMap().get(headers[i]);
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
            for (DepositFDTrxHistoryDTO dto : datas) {
                for (int i = 0; i < headers.length; i++) {
                    Function<DepositFDTrxHistoryDTO, String> function = this.getHeaderMap().get(headers[i]);
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

    private String convertTo12HourFormat(String input) {
        DateTimeFormatter inputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm", Locale.ENGLISH);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("dd MMM yyyy h:mm:ss a", Locale.ENGLISH);

        LocalDateTime dateTime = LocalDateTime.parse(input, inputFormatter);
        String result = dateTime.format(outputFormatter);
        return result;
    }

    private String formatCurrencyAmount(String currency, String amount, String creditDebitIndicator) {
        if (isNullOrEmpty(currency) || isNullOrEmpty(creditDebitIndicator) || isNullOrEmpty(amount)) {
            return "-";
        }
        try {
            String sanitizedAmount = amount.replace(",", "").trim();
            BigDecimal value = new BigDecimal(sanitizedAmount);
            DecimalFormat formatter = new DecimalFormat("#,##0.00");
            String formattedAmount = formatter.format(value);

            return creditDebitIndicator.equalsIgnoreCase("D")
                    ? "-" + currency + " " + formattedAmount
                    : currency + " " + formattedAmount;

        } catch (NumberFormatException e) {
            return "-";
        }
    }

    private boolean isNullOrEmpty(String value) {
        return value == null || value.isEmpty();
    }
}
