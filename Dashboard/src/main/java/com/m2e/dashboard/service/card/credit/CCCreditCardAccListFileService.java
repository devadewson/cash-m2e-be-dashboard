package com.m2e.dashboard.service.card.credit;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.card.credit.response.CCCreditCardListMYDTO;
import com.m2e.dashboard.service.FileService;
import com.m2e.dashboard.service.loan.overdraft.LNOverdraftFileService;
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
public class CCCreditCardAccListFileService implements FileService {

    private static final Logger log = LoggerFactory.getLogger(CCCreditCardAccListFileService.class);
    @Override
    public byte[] generateCsv(Object datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            List<CCCreditCardListMYDTO> rows = (List<CCCreditCardListMYDTO>) datas;
            // TODO : modify this logic based on real report format
            String[] headers = {"Card Name", "Card Number", "Available Credit Limit", "Outstanding Balance"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = rows.stream()
                    .map(dto -> new String[]{
                            dto.getCardName() != null ? dto.getCardName() : "",
                            dto.getCardNumber() != null && dto.getCardCurrency() != null ? dto.getCardNumber().concat(" - ").concat(dto.getCardCurrency()) : "",
                            dto.getCreditLimit() != null ? dto.getCreditLimit() : "",
                            dto.getOutstandingBalance() != null ? dto.getOutstandingBalance() : ""
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
            table.addHeaderCell("Card Name");
            table.addHeaderCell("Card Number");
            table.addHeaderCell("Available Credit Limit");
            table.addHeaderCell("Outstanding Balance");

            List<CCCreditCardListMYDTO> rows = (List<CCCreditCardListMYDTO>) datas;
            rows.stream().forEach(row -> {
                table.addCell(row.getCardName());
                table.addCell(row.getCardNumber().concat(" - ").concat(row.getCardCurrency()));
                table.addCell(row.getCreditLimit());
                table.addCell(row.getOutstandingBalance());
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
        sb.append("Card Name\tCard Number\tAvailable Credit Limit\tOutstanding Balance\n");

        List<CCCreditCardListMYDTO> rows = (List<CCCreditCardListMYDTO>) datas;
        rows.stream().forEach(row -> {
            sb.append(row.getCardName()).append("\t");
            sb.append(row.getNoOfCardMembers()).append("\t");
            sb.append(row.getCardNumber().concat(" - ").concat(row.getCardCurrency())).append("\t");
            sb.append(row.getCreditLimit()).append("\t");
            sb.append(row.getOutstandingBalance()).append("\n");
        });
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
