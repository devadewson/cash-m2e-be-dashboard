package com.m2e.dashboard.service.deposit.ca;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.dto.deposit.ca.response.DepositPaymentHistoryDTO;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
public class DepositCAPaymentHistFileService implements FileService<List<DepositPaymentHistoryDTO>> {

    @Override
    public byte[] generateCsv(List<DepositPaymentHistoryDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] headers = {"Date", "Bank Reference No", "Payment Type", "Transaction Type", "Transaction Amount", "Debit", "Credit"};
            csvWriter.writeNext(headers);

            for (DepositPaymentHistoryDTO dto : datas) {
                csvWriter.writeNext(buildRowArray(dto));
            }

            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<DepositPaymentHistoryDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<DepositPaymentHistoryDTO, String>> columnGetters = Arrays.asList(
                    DepositPaymentHistoryDTO::getDate,
                    DepositPaymentHistoryDTO::getBankRefNo,
                    DepositPaymentHistoryDTO::getPaymentTypeDesc,
                    DepositPaymentHistoryDTO::getTrxType,
                    DepositPaymentHistoryDTO::getTrxAmount,
                    DepositPaymentHistoryDTO::getDebit,
                    DepositPaymentHistoryDTO::getCredit
            );
            float totalTableWidth = calculateTotalTableWidth(datas, columnGetters);
            PageSize pageSize = getOptimalPageSize(totalTableWidth);

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            float[] columnWidths = calculateColumnWidths(datas, columnGetters, pageSize.getWidth());
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Date")));
            table.addHeaderCell(new Cell().add(new Paragraph("Bank Reference No")));
            table.addHeaderCell(new Cell().add(new Paragraph("Payment Type")));
            table.addHeaderCell(new Cell().add(new Paragraph("Transaction Type")));
            table.addHeaderCell(new Cell().add(new Paragraph("Transaction Amount")));
            table.addHeaderCell(new Cell().add(new Paragraph("Debit")));
            table.addHeaderCell(new Cell().add(new Paragraph("Credit")));

            for (DepositPaymentHistoryDTO row : datas) {
                String[] values = buildRowArray(row);
                //add cells in the same order as headers
                for (String v : values) {
                    table.addCell(new Cell().add(new Paragraph(v)));
                }
            }

            Paragraph title = new Paragraph("Deposit Current Account Payment Details Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold();
            document.add(title);
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<DepositPaymentHistoryDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s%-30s%-30s%-30s%-30s%-30s%-30s\n",
                "Date", "Bank Reference No", "Payment Type", "Transaction Type",
                "Transaction Amount", "Debit", "Credit"));

        for (DepositPaymentHistoryDTO row : datas) {
            String[] values = buildRowArray(row);
            String formattedRow = String.format("%-20s%-30s%-30s%-30s%-30s%-30s%-30s",
                    truncateOrPad(values[0], 20),
                    truncateOrPad(values[1], 30),
                    truncateOrPad(values[2], 30),
                    truncateOrPad(values[3], 30),
                    truncateOrPad(values[4], 30),
                    truncateOrPad(values[5], 30),
                    truncateOrPad(values[6], 30));
            sb.append(formattedRow).append("\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String[] buildRowArray(DepositPaymentHistoryDTO dto) {
        return new String[]{
                valueOrDefault(dto.getDate()),
                valueOrDefault(dto.getBankRefNo()),
                valueOrDefault(dto.getPaymentTypeDesc()),
                valueOrDefault(dto.getTrxType()),
                formatCurrency(dto.getCurrency(), dto.getTrxAmount()),
                formatCurrency(dto.getCreditDebitCurrency(), dto.getDebit()),
                formatCurrency(dto.getCreditDebitCurrency(), dto.getCredit())
        };
    }

    private String valueOrDefault(String value) {
        return value == null ? "-" : value;
    }

    private String formatCurrency(String currency, Object amount) {
        if (currency == null || amount == null) {
            return "-";
        }
        return currency + " " + amount;
    }

    private String truncateOrPad(String input, int length) {
        if (input == null) input = "";
        if (input.length() > length) {
            return input.substring(0, length);
        } else {
            return String.format("%-" + length + "s", input);
        }
    }
}
