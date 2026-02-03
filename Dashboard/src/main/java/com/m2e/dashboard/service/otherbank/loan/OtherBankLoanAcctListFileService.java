package com.m2e.dashboard.service.otherbank.loan;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.enums.ReportFormat;
import com.m2e.dashboard.service.FileService;
import com.m2e.dashboard.sp.otherbank.response.OtherBankLoanAcctListSpDTO;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
@Slf4j
public class OtherBankLoanAcctListFileService implements FileService<List<OtherBankLoanAcctListSpDTO>> {

    public byte[] generateFile(List<OtherBankLoanAcctListSpDTO> data, ReportFormat format) {
        return switch (format) {
            case CSV -> generateCsv(data);
            case PDF -> generatePdf(data);
            case TXT -> generateTxt(data);
            default -> throw new IllegalArgumentException("Report format is undefined");
        };
    }

    @Override
    public byte[] generateCsv(List<OtherBankLoanAcctListSpDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] headers = generateHeader();
            csvWriter.writeNext(headers);
            datas.forEach(dto -> {
                String[] dataRow = {
                        dto.getTrxDate() != null ? dto.getTrxDate() : "",
                        dto.getAcctName() != null && dto.getAcctNumber() != null && dto.getCurrency() != null ? dto.getAcctName().concat("\n").concat(dto.getAcctNumber().concat(" - ").concat(dto.getCurrency())) : "",
                        dto.getSenderBIC() != null ? dto.getSenderBIC() : "",
                        dto.getMsgType() != null ? dto.getMsgType() : "",
                        dto.getClosingBalance() != null ? dto.getClosingBalance() : ""
                };
                csvWriter.writeNext(dataRow);
            });
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<OtherBankLoanAcctListSpDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            Table table = new Table(5);
            String[] headers = generateHeader();
            for (String header : headers) {
                table.addHeaderCell(header);
            }
            datas.forEach(row -> {
                table.addCell(row.getTrxDate());
                table.addCell(row.getAcctName().concat("\n").concat(row.getAcctNumber().concat(" - ").concat(row.getCurrency())));
                table.addCell(row.getSenderBIC());
                table.addCell(row.getMsgType());
                table.addCell(row.getClosingBalance());
            });

            document.add(new Paragraph("Other Bank Loan View By Account Listing"));
            document.add(table);
            document.close();
            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<OtherBankLoanAcctListSpDTO> datas) {
        StringBuilder sb = new StringBuilder();
        String[] headers = generateHeader();
        for (String header : headers) {
            sb.append(header).append("\t");
        }
        sb.append("\n");
        datas.forEach(row -> sb.append(row.getTrxDate()).append("\t")
                .append(row.getAcctName().concat(" ").concat(row.getAcctNumber().concat(" - ").concat(row.getCurrency()))).append("\t")
                .append(row.getSenderBIC()).append("\t")
                .append(row.getMsgType()).append("\t\t")
                .append(row.getClosingBalance()).append("\n"));
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    public String[] generateHeader() {
        return new String[]{"Date\t\t", "Account Detail\t\t\t", "Sender BIC", "Message Type", "Closing Balance"};
    }
}
