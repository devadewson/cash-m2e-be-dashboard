package com.m2e.dashboard.service.deposit.fd;

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
import com.m2e.dashboard.sp.deposit.fd.response.DepositFDAcctSummarySpDTO;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
public class DepositFDAccountFileService {

    private static final Logger logger = LoggerFactory.getLogger(DepositFDAccountFileService.class);

    public byte[] generateCsv(List<DepositFDAcctSummarySpDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] headers = {"Account Name", "Account Number", "Currency", "No of Deposits", "Total Principal Amount"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getAcctName() != null ? dto.getAcctName() : "",
                            dto.getAcctNumber() != null ? dto.getAcctNumber()  : "",
                            dto.getCurrency() != null ? dto.getCurrency() : "",
                            dto.getNoOfCertificates() != null ? dto.getNoOfCertificates() : "",
                            dto.getTotalPrincipalAmt() != null ? dto.getTotalPrincipalAmt() : ""
                    })
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("generateCsv: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    public byte[] generatePdf(List<DepositFDAcctSummarySpDTO> datas, String provider) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        String pdfTitle = "";
        try {
            List<Function<DepositFDAcctSummarySpDTO, String>> columnGetters = Arrays.asList(
                    DepositFDAcctSummarySpDTO::getAcctName,
                    DepositFDAcctSummarySpDTO::getAcctNumber,
                    DepositFDAcctSummarySpDTO::getCurrency,
                    DepositFDAcctSummarySpDTO::getNoOfCertificates,
                    DepositFDAcctSummarySpDTO::getTotalPrincipalAmt
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
            table.addHeaderCell(new Cell().add(new Paragraph("No of Deposits")));
            table.addHeaderCell(new Cell().add(new Paragraph("Total Principal Amount")));

            datas.stream().forEach(row -> {
                table.addCell(new Cell().add(new Paragraph(row.getAcctName() != null ? row.getAcctName() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getAcctNumber() != null ? row.getAcctNumber() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getCurrency() != null ? row.getCurrency() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getNoOfCertificates() != null ? row.getNoOfCertificates() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getTotalPrincipalAmt() != null ? row.getTotalPrincipalAmt() : "")));
            });

            if ("SG".equalsIgnoreCase(provider)) {
                pdfTitle = "Time Deposit Accounts Report";
            } else {
                pdfTitle = "Fixed Deposit Accounts Report";
            }

            Paragraph title = new Paragraph(pdfTitle)
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

    public byte[] generateTxt(List<DepositFDAcctSummarySpDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-140s%-20s%-10s%-20s%-30s\n",
                "Account Name", "Account Number",
                "Currency", "No of Deposits", "Total Principal Amount"));

        datas.forEach(row -> {
            String acctName = row.getAcctName() != null ? row.getAcctName() : "";
            String acctNumber = row.getAcctNumber() != null ? row.getAcctNumber() : "";
            String currency = row.getCurrency() != null ? row.getCurrency() : "";
            String noOfCertificates = row.getNoOfCertificates() != null ? row.getNoOfCertificates() : "";
            String totalPrincipalAmt = row.getTotalPrincipalAmt() != null ? row.getTotalPrincipalAmt() : "";

            String formattedRow = String.format("%-140s%-20s%-10s%-20s%-30s",
                    truncateOrPad(acctName, 140),
                    truncateOrPad(acctNumber, 20),
                    truncateOrPad(currency, 3),
                    truncateOrPad(noOfCertificates, 10),
                    truncateOrPad(totalPrincipalAmt, 20));
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
