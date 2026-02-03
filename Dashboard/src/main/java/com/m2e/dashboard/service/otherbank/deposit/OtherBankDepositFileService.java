package com.m2e.dashboard.service.otherbank.deposit;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.service.FileService;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import com.m2e.dashboard.sp.otherbank.response.OtherBankDepositAcctListingSpDTO;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
public class OtherBankDepositFileService implements FileService<List<OtherBankDepositAcctListingSpDTO>> {
    @Override
    public byte[] generateCsv(List<OtherBankDepositAcctListingSpDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            List<OtherBankDepositAcctListingSpDTO> rows = (List<OtherBankDepositAcctListingSpDTO>) datas;
            // TODO : modify this logic based on real report format
            String[] headers = {"Date", "Account Details", "Sender BIC", "Message Type", "Closing Balance"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = rows.stream()
                    .map(dto -> new String[]{
                            dto.getDateTime() != null ? dto.getDateTime() : "",
                            dto.getAcctName() != null && dto.getAcctNumber() != null && dto.getCurrency() != null ? dto.getAcctName() + "\n" + dto.getAcctNumber() + " - " + dto.getCurrency()  : "",
                            dto.getSenderBIC() != null ? dto.getSenderBIC() : "",
                            dto.getMessageType() != null ? dto.getMessageType() : "",
                            dto.getClosingBalance() != null ? dto.getClosingBalance() : ""
                    })
                    .collect(Collectors.toList());
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<OtherBankDepositAcctListingSpDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            Table table = new Table(5);
            table.addHeaderCell("Date");
            table.addHeaderCell("Account Details");
            table.addHeaderCell("Sender BIC");
            table.addHeaderCell("Message Type");
            table.addHeaderCell("Closing Balance");

            List<OtherBankDepositAcctListingSpDTO> rows = (List<OtherBankDepositAcctListingSpDTO>) datas;
            rows.stream().forEach(row -> {
                table.addCell(row.getDateTime() != null ? row.getDateTime() : "");
                table.addCell(row.getAcctName() != null
                        && row.getAcctNumber() != null
                        && row.getCurrency() != null ? row.getAcctName() + " " + row.getAcctNumber() + " - " + row.getCurrency() : "");
                table.addCell(row.getSenderBIC() != null ? row.getSenderBIC() : "");
                table.addCell(row.getMessageType() != null ? row.getMessageType() : "");
                table.addCell(row.getClosingBalance() != null ? row.getClosingBalance() : "");
            });

            Paragraph title = new Paragraph("Other Bank - Deposit Report")
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
    public byte[] generateTxt(List<OtherBankDepositAcctListingSpDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s%-140s%-20s%-30s%-20s\n",
                "Date", "Account Details", "Sender BIC",
                "Message Type", "Closing Balance"));

        datas.forEach(row -> {
            String date = row.getDateTime() != null ? row.getDateTime() : "";
            String acctDetails = row.getAcctName() != null
                    && row.getAcctNumber() != null
                    && row.getCurrency() != null ? row.getAcctName() + " " + row.getAcctNumber() + " - " + row.getCurrency() : "";
            String senderBIC = row.getSenderBIC() != null ? row.getSenderBIC() : "";
            String msgType = row.getMessageType() != null ? row.getMessageType() : "";
            String closingBalance = row.getClosingBalance() != null ? row.getClosingBalance() : "";

            String formattedRow = String.format("%-20s%-140s%-20s%-30s%-20s",
                    truncateOrPad(date, 20),
                    truncateOrPad(acctDetails, 140),
                    truncateOrPad(senderBIC, 20),
                    truncateOrPad(msgType, 30),
                    truncateOrPad(closingBalance, 20));
            sb.append(formattedRow).append("\n");
        });

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String truncateOrPad(String input, int length) {
        if (input.length() > length) {
            return input.substring(0, length);
        } else {
            return String.format("%-" + length + "s", input);
        }
    }
}
