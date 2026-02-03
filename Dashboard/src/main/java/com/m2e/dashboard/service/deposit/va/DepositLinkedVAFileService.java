package com.m2e.dashboard.service.deposit.va;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.deposit.va.response.DepositVASearchDto;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class DepositLinkedVAFileService implements FileService<List<DepositVASearchDto>> {
    String[] headers = {"Corporate Code","Virtual Acc Name", "Virtual Acc No", "Virtual Acc Ref No"};

   @Override
   public byte[] generateTxt(List<DepositVASearchDto> rows) {

       StringBuilder sb = new StringBuilder();
       sb.append(String.format("%-30s%-50s%-50s%-50s%n",
                "Corporate Code", "Virtual Account Name","Virtual Account No", "Virtual Account Reference No"));

        rows.forEach(row -> {
                String corpCode = row.getCorpCode() != null ? row.getCorpCode() : "";
                String virtualAccountName = row.getVirtualAcctName() != null ? row.getVirtualAcctName() : "";
                String virtualAccountNo = row.getVirtualAcctNumber() != null ? row.getVirtualAcctNumber() : "";
                String virtualAccountRef = row.getVirtualAcctRefNumber() != null ? row.getVirtualAcctRefNumber() : "";

                String formattedRow = String.format("%-30s%-50s%-50s%-50s",
                        truncateOrPad(corpCode, 30),
                        truncateOrPad(virtualAccountName, 50),
                        truncateOrPad(virtualAccountNo, 50),
                        truncateOrPad(virtualAccountRef, 50));
                sb.append(formattedRow).append("\n");
        });

        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
    @Override
    public byte[] generateCsv(List<DepositVASearchDto> rows) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(headers);
            List<String[]> dataRows = rows.stream()
                    .map(dto -> new String[]{
                            dto.getCorpCode() != null ? dto.getCorpCode() : "",
                            dto.getVirtualAcctName() != null ? dto.getVirtualAcctName() : "",
                            dto.getVirtualAcctNumber() != null ? dto.getVirtualAcctNumber() : "",
                            dto.getVirtualAcctRefNumber() != null ? dto.getVirtualAcctRefNumber() : ""
                    })
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<DepositVASearchDto> rows) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            Table table = getTable(rows);
            document.add(new Paragraph("Virtual Account Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    private Table getTable(List<DepositVASearchDto> data) {
        Table table = new Table(4);
        table.addHeaderCell("Corporate Code");
        table.addHeaderCell("Virtual Acc Name");
        table.addHeaderCell("Virtual Acc No");
        table.addHeaderCell("Virtual Acc Ref No");

        data.forEach(row -> {
                table.addCell(row.getCorpCode());
                table.addCell(row.getVirtualAcctName());
                table.addCell(String.valueOf(row.getVirtualAcctNumber()));
                table.addCell(row.getVirtualAcctRefNumber());
        });
        return table;
    }

    private String truncateOrPad(String input, int length) {
        if (input.length() > length) {
            return input.substring(0, length);
        } else {
            return String.format("%-" + length + "s", input);
        }
    }
}
