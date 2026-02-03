package com.m2e.dashboard.service.loan.tlf;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.loan.tlf.response.LNTLFTrxHistoryDTO;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class LNTLFTrxHistoryFileService implements FileService<List<LNTLFTrxHistoryDTO>> {

    private final String[] HEADERS = {
            "Date",
            "Transaction Reference",
            "Transaction Description",
            "Debit",
            "Credit"
    };
    private final int DATE_TXT_WIDTH = 20;
    private final int TRX_REF_TXT_WIDTH = 30;
    private final int TRX_DESC_TXT_WIDTH = 30;
    private final int DEBIT_TXT_WIDTH = 30;
    private final int CREDIT_TXT_WIDTH = 30;
    private final String TXT_ROW_FORMAT = "%-" + DATE_TXT_WIDTH + "s"
            + "%-" + TRX_REF_TXT_WIDTH + "s"
            + "%-" + TRX_DESC_TXT_WIDTH + "s"
            + "%-" + DEBIT_TXT_WIDTH + "s"
            + "%-" + CREDIT_TXT_WIDTH + "s";

    @Override
    public byte[] generateCsv(List<LNTLFTrxHistoryDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(HEADERS);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getDate() != null ? dto.getDate() : "-",
                            dto.getTrxReference() != null ? dto.getTrxReference() : "-",
                            dto.getTrxDesc() != null ? dto.getTrxDesc() : "-",
                            dto.getDebit() != null ? dto.getDebit() : "-",
                            dto.getCredit() != null ? dto.getCredit() : "-"
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
    public byte[] generatePdf(List<LNTLFTrxHistoryDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            Table table = new Table(HEADERS.length);
            for (String header : HEADERS) {
                table.addHeaderCell(header);
            }

            datas.forEach(row -> {
                table.addCell(row.getDate());
                table.addCell(row.getTrxReference());
                table.addCell(row.getTrxDesc());
                table.addCell(Objects.isNull(row.getDebit()) ? "-" : row.getDebit());
                table.addCell(Objects.isNull(row.getCredit()) ? "-" : row.getCredit());
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
    public byte[] generateTxt(List<LNTLFTrxHistoryDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(TXT_ROW_FORMAT + "\n",
                HEADERS[0], HEADERS[1], HEADERS[2], HEADERS[3], HEADERS[4]));

        datas.forEach(row -> {
            String date = row.getDate();
            String trxRef = row.getTrxReference();
            String trxDesc = row.getTrxDesc();
            String debit = Objects.isNull(row.getDebit()) ? "-" : row.getDebit();
            String credit = Objects.isNull(row.getCredit()) ? "-" : row.getCredit();

            String formattedRow = String.format(TXT_ROW_FORMAT,
                    truncateOrPad(date, DATE_TXT_WIDTH),
                    truncateOrPad(trxRef, TRX_REF_TXT_WIDTH),
                    truncateOrPad(trxDesc, TRX_DESC_TXT_WIDTH),
                    truncateOrPad(debit, DEBIT_TXT_WIDTH),
                    truncateOrPad(credit, CREDIT_TXT_WIDTH));
            sb.append(formattedRow).append("\n");
        });
        return sb.toString().getBytes(StandardCharsets.UTF_8);
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
