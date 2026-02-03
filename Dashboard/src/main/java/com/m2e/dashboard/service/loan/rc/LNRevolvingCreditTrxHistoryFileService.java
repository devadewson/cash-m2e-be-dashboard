package com.m2e.dashboard.service.loan.rc;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.loan.rc.response.LNRevolvingCreditTrxHistoryDTO;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Objects;

@Service
@Slf4j
public class LNRevolvingCreditTrxHistoryFileService implements FileService<List<LNRevolvingCreditTrxHistoryDTO>> {

    private static final Logger log = LoggerFactory.getLogger(LNRevolvingCreditTrxHistoryFileService.class);

    @Override
    public byte[] generateCsv(List<LNRevolvingCreditTrxHistoryDTO> datas) {
        log.info("generateCsv datas: {}", datas);
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            // TODO : modify this logic based on real report format
            String[] headers = {"Date","Transaction Reference", "Transaction Description", "Debit", "Credit"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getDate() != null ? dto.getDate() : "",
                            dto.getTrxReference() != null ? dto.getTrxReference() : "",
                            dto.getTrxDesc() != null ? dto.getTrxDesc() : "",
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
    public byte[] generatePdf(List<LNRevolvingCreditTrxHistoryDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);
            // TODO : modify this logic based on real report format
            Table table = new Table(5);
            table.addHeaderCell("Date");
            table.addHeaderCell("Transaction Reference");
            table.addHeaderCell("Transaction Description");
            table.addHeaderCell("Debit");
            table.addHeaderCell("Credit");

            datas.stream().forEach(row -> {
                table.addCell(row.getDate());
                table.addCell(row.getTrxReference());
                table.addCell(row.getTrxDesc());
                table.addCell(Objects.isNull(row.getDebit()) ? "-" : row.getDebit());
                table.addCell(Objects.isNull(row.getCredit()) ? "-" : row.getCredit());
            });

            document.add(new Paragraph("Loan Revolving Credit Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("generatePdf: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<LNRevolvingCreditTrxHistoryDTO> datas) {
        log.info("generateTxt datas: {}", datas);
        StringBuilder sb = new StringBuilder();
        // TODO : modify this logic based on real report format
        sb.append("Date\tTransaction Reference\tTransaction Description\tDebit\tCredit\n");

        datas.stream().forEach(row -> {
            sb.append(row.getDate()).append("\t");
            sb.append(row.getTrxReference()).append("\t");
            sb.append(row.getTrxDesc()).append("\t");
            sb.append(Objects.isNull(row.getDebit()) ? "-" : row.getDebit()).append("\t");
            sb.append(Objects.isNull(row.getCredit()) ? "-" : row.getCredit()).append("\n");
        });
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
