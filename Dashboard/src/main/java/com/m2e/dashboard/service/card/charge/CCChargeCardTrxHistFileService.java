package com.m2e.dashboard.service.card.charge;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.card.common.response.CCTrxHistoryDTO;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CCChargeCardTrxHistFileService implements FileService {

    private static final Logger log = LoggerFactory.getLogger(CCChargeCardTrxHistFileService.class);
    @Override
    public byte[] generateCsv(Object datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            List<CCTrxHistoryDTO> rows = (List<CCTrxHistoryDTO>) datas;
            // TODO : modify this logic based on real report format
            String[] headers = {"Transaction Date", "Transaction Description", "Debit", "Credit"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = rows.stream()
                    .map(dto -> new String[]{
                            dto.getTrxDate() != null ? dto.getTrxDate() : "",
                            dto.getTrxDescription() != null ? dto.getTrxDescription() : "",
                            dto.getDebit() != null ? dto.getDebit() : "",
                            dto.getCredit() != null ? dto.getCredit() : ""
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
            // TODO : modify this logic based on real report format
            Table table = new Table(5);
            table.addHeaderCell("Transaction Date");
            table.addHeaderCell("Transaction Description");
            table.addHeaderCell("Debit");
            table.addHeaderCell("Credit");

            List<CCTrxHistoryDTO> rows = (List<CCTrxHistoryDTO>) datas;
            rows.stream().forEach(row -> {
                table.addCell(row.getTrxDate());
                table.addCell(row.getTrxDescription());
                table.addCell(row.getDebit());
                table.addCell(row.getCredit());
            });

            document.add(new Paragraph("Credit Card Report"));
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
        // TODO : modify this logic based on real report format
        sb.append("Transaction Date\tTransaction Description\tDebit\tCredit\n");

        List<CCTrxHistoryDTO> rows = (List<CCTrxHistoryDTO>) datas;
        rows.stream().forEach(row -> {
            sb.append(row.getTrxDate()).append("\t");
            sb.append(row.getTrxDescription()).append("\t");
            sb.append(row.getDebit()).append("\t");
            sb.append(row.getCredit()).append("\n");
        });
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
