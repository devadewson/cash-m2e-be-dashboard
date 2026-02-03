package com.m2e.dashboard.service.inv;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.service.FileService;
import com.m2e.dashboard.sp.investment.response.INVUnitTrustSearchSpDTO;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class INVUnitTrustFileService implements FileService<List<INVUnitTrustSearchSpDTO>> {
    String accountName = "Account Name";
    String accountNumber = "Account Number";
    String noofFunds = "No of Funds";
    String lastClosingNav = "Last Closing NAV";
    String estMarketValue = "Total Est Market Value";
    @Override
    public byte[] generateCsv(List<INVUnitTrustSearchSpDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] headers = {accountName, accountNumber, noofFunds, estMarketValue};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getAcctName() != null ? dto.getAcctName() : "",
                            dto.getAcctNumber() != null ? dto.getAcctNumber().concat(" - ").concat(dto.getCurrency()) : "",
                            String.valueOf(dto.getNoOfFunds() != null ? dto.getNoOfFunds() : 0),
                            String.valueOf(dto.getTotalEstMarketValue() != null ? dto.getTotalEstMarketValue() : 0)
                    })
                    .toList();

            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<INVUnitTrustSearchSpDTO> datas) {
        log.info("here is generate PDF :: " + datas.toString());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Table table = new Table(4);
            table.addHeaderCell(accountName);
            table.addHeaderCell(accountNumber);
            table.addHeaderCell(noofFunds);
            table.addHeaderCell(estMarketValue);

            datas.forEach(row -> {
                table.addCell(row.getAcctName() != null ? row.getAcctName() : "");
                table.addCell(row.getAcctNumber() != null ? row.getAcctNumber().concat(" - ").concat(row.getCurrency()) : "");
                table.addCell(row.getNoOfFunds() != null ? String.valueOf(row.getNoOfFunds()) : "");
                table.addCell(row.getTotalEstMarketValue() != null ? row.getTotalEstMarketValue() : "");
            });

            document.add(new Paragraph("Unit Trust Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.info("here is generate PDF null :: ");
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<INVUnitTrustSearchSpDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-100s%-30s%-20s%-20s%n",
                accountName, accountNumber, noofFunds, estMarketValue));

        datas.forEach(row -> {
            String acctName = row.getAcctName() != null ? row.getAcctName() : "";
            String acctNumber = row.getAcctNumber() != null ? row.getAcctNumber().concat(" - ").concat(row.getCurrency()) : "";
            String noOfFunds = row.getNoOfFunds() != null ? String.valueOf(row.getNoOfFunds()) : "";
            String totalEstMarketValue = row.getTotalEstMarketValue() != null ? row.getTotalEstMarketValue() : "";

            String formattedRow = String.format("%-100s%-30s%-20s%-20s",
                    truncateOrPad(acctName, 100),
                    truncateOrPad(acctNumber, 30),
                    truncateOrPad(noOfFunds, 20),
                    truncateOrPad(totalEstMarketValue, 20));
            sb.append(formattedRow).append("\n");
        });
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String truncateOrPad(String input, int length) {
        if (input.length() > length) {
            return input.substring(0, length);
        } else {
            String formatString = "%-" + length + "s";
            return String.format(formatString, input);
        }
    }
}
