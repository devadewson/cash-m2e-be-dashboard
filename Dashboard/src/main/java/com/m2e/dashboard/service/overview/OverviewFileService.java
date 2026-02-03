package com.m2e.dashboard.service.overview;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.service.FileService;
import com.m2e.dashboard.sp.overview.response.OverviewSpDTO;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OverviewFileService implements FileService {

    DecimalFormat df = new DecimalFormat("#,##0.00");

    @Override
    public byte[] generateCsv(Object datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            List<OverviewSpDTO> rows = (List<OverviewSpDTO>) datas;
            String[] headers = {"Account(s)", "No. of Account", "Ledger Balance", "Available Balance", "Facility Limit", "Outstanding Balance"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = rows.stream()
                    .map(dto -> new String[]{
                            dto.getSubProductDesc(),
                            formatNoOfAcct(dto.getNoOfAccounts()),
                            formatAmountExcel(dto.getLedgerBalance(), df),
                            formatAmountExcel(dto.getAvailableBalance(), df),
                            formatAmountExcel(dto.getFacilityLimit(), df),
                            formatAmountExcel(dto.getOutstandingBalance(), df)
                    })
                    .collect(Collectors.toList());
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("generateCsv: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(Object datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Table table = new Table(6);
            table.addHeaderCell("Account(s)");
            table.addHeaderCell("No. of Account");
            table.addHeaderCell("Ledger Balance");
            table.addHeaderCell("Available Balance");
            table.addHeaderCell("Facility Limit");
            table.addHeaderCell("Outstanding Balance");

            List<OverviewSpDTO> rows = (List<OverviewSpDTO>) datas;
            rows.stream().forEach(row -> {
                table.addCell(row.getSubProductDesc());
                table.addCell(formatNoOfAcct(row.getNoOfAccounts()));
                table.addCell(formatAmountPdf(row.getLedgerBalance()));
                table.addCell(formatAmountPdf(row.getAvailableBalance()));
                table.addCell(formatAmountPdf(row.getFacilityLimit()));
                table.addCell(formatAmountPdf(row.getOutstandingBalance()));
            });

            document.add(new Paragraph("Portfolio Overview Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("generatePdf: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(Object datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-100s%-20s%-20s%-20s%-20s%-20s\n",
                "Account(s)", "No. of Account",
                "Ledger Balance", "Available Balance",
                "Facility Limit","Outstanding Balance"));

        List<OverviewSpDTO> rows = (List<OverviewSpDTO>) datas;

        rows.stream().forEach(row -> {
            String subProductDesc = row.getSubProductDesc() != null ? row.getSubProductDesc() : "";
            String noOfAcct = row.getNoOfAccounts() != null ? row.getNoOfAccounts() : "0";
            String ledgerBal = row.getLedgerBalance() != null ? String.valueOf(row.getLedgerBalance()) : "-";
            String availBal = row.getAvailableBalance() != null ? String.valueOf(row.getAvailableBalance()) : "-";
            String facilityLimit = row.getFacilityLimit() != null ? String.valueOf(row.getFacilityLimit()) : "-";
            String outstandingBalance = row.getOutstandingBalance() != null ? String.valueOf(row.getOutstandingBalance()) : "-";

            String formattedRow = String.format("%-100s%-20s%-20s%-20s%-20s%-20s",
                    truncateOrPad(subProductDesc, 100),
                    truncateOrPad(noOfAcct, 20),
                    truncateOrPad(ledgerBal, 20),
                    truncateOrPad(availBal, 20),
                    truncateOrPad(facilityLimit, 20),
                    truncateOrPad(outstandingBalance, 20));

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

    private String formatAmountExcel(BigDecimal amount, DecimalFormat df) {
        if (amount == null) {
            return "-";
        }
        String formatted = df.format(amount.doubleValue()).replace("\"", "\"\"");
        return "=TEXT(\"" + formatted + "\", \"#,##0.00\")";
    }

    private String formatAmountPdf(BigDecimal amount) {
        if (amount == null) {
            return "-";
        }
        return String.valueOf(amount);
    }

    private String formatNoOfAcct(String noOfAcct) {
        if (noOfAcct == null) {
            return "0";
        }
        return noOfAcct;
    }

}
