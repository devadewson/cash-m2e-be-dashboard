package com.m2e.dashboard.service.loan.tlf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.loan.tlf.response.LNTLFDTO;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class LNTLFFileService implements FileService<List<LNTLFDTO>> {

    private static final Logger log = LoggerFactory.getLogger(LNTLFFileService.class);
    @Override
    public byte[] generateCsv(List<LNTLFDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            // TODO : modify this logic based on real report format
            String[] headers = {"Account Name", "Account Number", "Drawdown Amount", "Outstanding Balance"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getAcctName() != null ? dto.getAcctName() : "-",
                            dto.getAcctNumber() != null && dto.getAccountCurrency() != null ? dto.getAcctNumber().concat(" - ").concat(dto.getAccountCurrency()) : "-",
                            dto.getDrawdownAmount() != null ? dto.getDrawdownAmount() : "-",
                            dto.getOutstandingBalance() != null ? dto.getOutstandingBalance() : "-"
                    })
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("generateCsv: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<LNTLFDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            // TODO : modify this logic based on real report format
            Table table = new Table(4);
            table.addHeaderCell("Account Name");
            table.addHeaderCell("Account Number");
            table.addHeaderCell("Drawdown Amount");
            table.addHeaderCell("Outstanding Balance");

            datas.stream().forEach(row -> {
                table.addCell(row.getAcctName() != null ? row.getAcctName() : "-");
                table.addCell(row.getAcctNumber() != null && row.getAccountCurrency() != null ? row.getAcctNumber().concat(" - ").concat(row.getAccountCurrency()) : "-");
                table.addCell(row.getDrawdownAmount() != null ? row.getDrawdownAmount() : "-");
                table.addCell(row.getOutstandingBalance() != null ? row.getOutstandingBalance() : "-");
            });

            document.add(new Paragraph("Loan Term Loan Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("generatePdf: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<LNTLFDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-140s%-20s%-20s%-20s%n",
                "Account Name", "Account Number",
                "Drawdown Amount", "Outstanding Balance"));

        datas.forEach(row -> {
            String acctName = row.getAcctName() != null ? row.getAcctName() : "-";
            String acctNumber = row.getAcctNumber() != null && row.getAccountCurrency() != null ? row.getAcctNumber().concat(" - ").concat(row.getAccountCurrency()) : "-";
            String drawdownAmount = row.getDrawdownAmount() != null ? row.getDrawdownAmount() : "-";
            String outstandingBalance = row.getOutstandingBalance() != null ? row.getOutstandingBalance() : "-";

            String formattedRow = String.format("%-140s%-20s%-20s%-20s",
                    truncateOrPad(acctName, 140),
                    truncateOrPad(acctNumber, 20),
                    truncateOrPad(drawdownAmount, 20),
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
}
