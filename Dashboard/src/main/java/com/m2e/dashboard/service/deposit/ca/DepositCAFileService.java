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
import com.m2e.dashboard.service.FileService;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.*;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
public class DepositCAFileService implements FileService<List<DepositAcctSummarySpDTO>> {

    private static final Logger logger = LoggerFactory.getLogger(DepositCAFileService.class);

    @Override
    public byte[] generateCsv(List<DepositAcctSummarySpDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            // TODO : modify this logic based on real report format
            String[] headers = {"Account Name", "Account Number", "Currency", "Ledger Balance", "Available Balance"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getAcctName() != null ? dto.getAcctName() : "",
                            dto.getAcctNumber() != null ? dto.getAcctNumber()  : "",
                            dto.getCurrency() != null ? dto.getCurrency() : "",
                            dto.getLedgerBalance() != null ? dto.getLedgerBalance() : "",
                            dto.getAvailableBalance() != null ? dto.getAvailableBalance() : ""
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
    public byte[] generatePdf(List<DepositAcctSummarySpDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<DepositAcctSummarySpDTO, String>> columnGetters = Arrays.asList(
                    DepositAcctSummarySpDTO::getAcctName,
                    DepositAcctSummarySpDTO::getAcctNumber,
                    DepositAcctSummarySpDTO::getCurrency,
                    DepositAcctSummarySpDTO::getLedgerBalance,
                    DepositAcctSummarySpDTO::getAvailableBalance
            );
            float totalTableWidth = calculateTotalTableWidth(datas, columnGetters);
            PageSize pageSize = getOptimalPageSize(totalTableWidth);

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, pageSize);

            float[] columnWidths = calculateColumnWidths(datas, columnGetters, pageSize.getWidth());
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Account Name")));
            table.addHeaderCell(new Cell().add(new Paragraph("Account Number")));
            table.addHeaderCell(new Cell().add(new Paragraph("Currency")));
            table.addHeaderCell(new Cell().add(new Paragraph("Ledger Balance")));
            table.addHeaderCell(new Cell().add(new Paragraph("Available Balance")));

            datas.stream().forEach(row -> {
                table.addCell(new Cell().add(new Paragraph(row.getAcctName() != null ? row.getAcctName() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getAcctNumber() != null ? row.getAcctNumber() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getCurrency() != null ? row.getCurrency() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getLedgerBalance() != null ? row.getLedgerBalance() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getAvailableBalance() != null ? row.getAvailableBalance() : "")));
            });

            Paragraph title = new Paragraph("Deposit Current Accounts Report")
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
    public byte[] generateTxt(List<DepositAcctSummarySpDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-140s%-20s%-10s%-20s%-20s\n",
                "Account Name", "Account Number",
                "Currency", "Ledger Balance", "Available Balance"));

        datas.forEach(row -> {
            String acctName = row.getAcctName() != null ? row.getAcctName() : "";
            String acctNumber = row.getAcctNumber() != null ? row.getAcctNumber() : "";
            String currency = row.getCurrency() != null ? row.getCurrency() : "";
            String ledgerBalance = row.getLedgerBalance() != null ? row.getLedgerBalance() : "";
            String availableBalance = row.getAvailableBalance() != null ? row.getAvailableBalance() : "";

            String formattedRow = String.format("%-140s%-20s%-10s%-20s%-20s",
                    truncateOrPad(acctName, 140),
                    truncateOrPad(acctNumber, 20),
                    truncateOrPad(currency, 3),
                    truncateOrPad(ledgerBalance, 20),
                    truncateOrPad(availableBalance, 20));
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
