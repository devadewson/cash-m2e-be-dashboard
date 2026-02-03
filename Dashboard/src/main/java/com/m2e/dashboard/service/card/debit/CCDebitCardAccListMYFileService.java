package com.m2e.dashboard.service.card.debit;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.card.debit.response.CCDebitCardListMYDTO;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CCDebitCardAccListMYFileService implements FileService {
    @Override
    public byte[] generateCsv(Object datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            List<CCDebitCardListMYDTO> rows = (List<CCDebitCardListMYDTO>) datas;
            // TODO : modify this logic based on real report format
            String[] headers = {"Card Name", "Card Number", "Monthly Purchasing Limit"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = rows.stream()
                    .map(dto -> new String[]{
                            dto.getCardName() != null ? dto.getCardName() + "\n" + dto.getCardHolderName() : "",
                            dto.getCardNumber() != null && dto.getCardCurrency() != null ? dto.getCardNumber().concat(" - ").concat(dto.getCardCurrency()) : "",
                            dto.getMonthlyPurchasingLimit() != null ? dto.getMonthlyPurchasingLimit() : ""
                    })
                    .collect(Collectors.toList());
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            e.printStackTrace();
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
            table.addHeaderCell("Monthly Purchasing Limit");

            List<CCDebitCardListMYDTO> rows = (List<CCDebitCardListMYDTO>) datas;
            rows.stream().forEach(row -> {
                table.addCell(row.getCardName() + "\n" + row.getCardHolderName());
                table.addCell(row.getCardNumber().concat(" - ").concat(row.getCardCurrency()));
                table.addCell(row.getMonthlyPurchasingLimit());
            });

            document.add(new Paragraph("Debit Card Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(Object datas) {
        StringBuilder sb = new StringBuilder();
        // TODO : modify this logic based on real report format
        sb.append("Card Name\tCard Number\tMonthly Purchasing Limit\n");

        List<CCDebitCardListMYDTO> rows = (List<CCDebitCardListMYDTO>) datas;
        rows.stream().forEach(row -> {
            sb.append(row.getCardName()).append("\n");
            sb.append(row.getCardHolderName()).append("\t");
            sb.append(row.getCardNumber().concat(" - ").concat(row.getCardCurrency())).append("\t");
            sb.append(row.getMonthlyPurchasingLimit()).append("\n");
        });
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }
}
