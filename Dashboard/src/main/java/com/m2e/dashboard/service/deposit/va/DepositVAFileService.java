package com.m2e.dashboard.service.deposit.va;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.deposit.va.response.DepositVASearchDto;
import com.m2e.dashboard.dto.deposit.va.response.DepositVASubSearchDTO;
import com.m2e.dashboard.service.FileService;
import com.m2e.dashboard.sp.deposit.ca.response.DepositAcctSummarySpDTO;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DepositVAFileService implements FileService<List<DepositVASubSearchDTO>> {
    @Override
    public byte[] generateCsv(List<DepositVASubSearchDTO> rows) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] headers = {"Current Account", "Corporate Code", "Virtual Account Name","Virtual Account No", "Virtual Account Reference No"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = rows.stream()
                    .flatMap(dto -> dto.getVirtualAccount().stream()  // Use flatMap to process all virtual accounts
                            .map(virtualAccount -> new String[]{
                                    dto.getCurrAcctNumber() != null ? dto.getCurrAcctNumber() : "",
                                    virtualAccount.getCorpCode() != null ? virtualAccount.getCorpCode() : "",
                                    virtualAccount.getVirtualAcctName() != null ? virtualAccount.getVirtualAcctName() : "",
                                    virtualAccount.getVirtualAcctNumber() != null ? virtualAccount.getVirtualAcctNumber() : "",
                                    virtualAccount.getVirtualAcctRefNumber() != null ? virtualAccount.getVirtualAcctRefNumber() : ""
                            })
                    )
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<DepositVASubSearchDTO> rows) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            Table table = new Table(5);
            table.addHeaderCell("Current Account");
            table.addHeaderCell("Corporate Code");
            table.addHeaderCell("Virtual Acc Name");
            table.addHeaderCell("Virtual Acc No");
            table.addHeaderCell("Virtual Acc Ref No");

            rows.forEach(row -> {
                for (DepositVASearchDto virtualAccount : row.getVirtualAccount()) {
                    // Add the virtual account details
                    table.addCell(row.getCurrAcctNumber());
                    table.addCell(virtualAccount.getCorpCode());
                    table.addCell(virtualAccount.getVirtualAcctName());
                    table.addCell(String.valueOf(virtualAccount.getVirtualAcctNumber()));
                    table.addCell(virtualAccount.getVirtualAcctRefNumber());
                }
            });
            document.add(new Paragraph("Virtual Account Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt( List<DepositVASubSearchDTO> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-50s%-30s%-50s%-50s%-50s%n",
                "Current Account", "Corporate Code", "Virtual Account Name","Virtual Account No", "Virtual Account Reference No"));

        rows.forEach(row -> {
            for (DepositVASearchDto virtualAccount : row.getVirtualAccount()) {
                String currAccNo = row.getCurrAcctNumber();
                String corpCode = virtualAccount.getCorpCode() != null ? virtualAccount.getCorpCode() : "";
                String virtualAccountName = virtualAccount.getVirtualAcctName() != null ? virtualAccount.getVirtualAcctName() : "";
                String virtualAccountNo = virtualAccount.getVirtualAcctNumber() != null ? virtualAccount.getVirtualAcctNumber() : "";
                String virtualAccountRef = virtualAccount.getVirtualAcctRefNumber() != null ? virtualAccount.getVirtualAcctRefNumber() : "";

                String formattedRow = String.format("%-50s%-30s%-50s%-50s%-50s",
                        truncateOrPad(currAccNo, 50),
                        truncateOrPad(corpCode, 30),
                        truncateOrPad(virtualAccountName, 50),
                        truncateOrPad(virtualAccountNo, 50),
                        truncateOrPad(virtualAccountRef, 50));
                sb.append(formattedRow).append("\n");
            }
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
