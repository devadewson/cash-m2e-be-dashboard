package com.m2e.dashboard.service.deposit.ca;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.AreaBreakType;
import com.itextpdf.layout.properties.TextAlignment;
import com.m2e.dashboard.dto.common.ContentDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositCAGPIBankDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositCAGPIStatusDTO;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.util.List;


@Service
public class DepositCAGPIStatusFileService {
    public byte[] generatePdf(DepositCAGPIStatusDTO statusRes) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        // Add Title
        document.add(new Paragraph("Deposit Current Accounts GPI Status Report")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14)
                .setBold());

        // First page content
        document.add(new Paragraph("UETR Number: " + (statusRes.getUetrNumber() != null ? statusRes.getUetrNumber() : "")));
        document.add(new Paragraph("GPI Status: " +  (statusRes.getGpiStatus() != null ? statusRes.getGpiStatus() : "")));
        document.add(new Paragraph("GPI Reason: " +  (statusRes.getGpiReason() != null ? statusRes.getGpiReason() : "")));
        document.add(new Paragraph("Total Elapsed Time: " +  (statusRes.getTotalElapsedTime() != null ? statusRes.getTotalElapsedTime() : "")));

        // Subsequent pages for each bank
        List<DepositCAGPIBankDTO> bankList = statusRes.getBankList();
        if (bankList != null) {
            for (DepositCAGPIBankDTO bankRes : bankList) {
                document.add(new AreaBreak(AreaBreakType.NEXT_PAGE)); // New page for each bank
                document.add(new Paragraph("Bank: " +  (bankRes.getBank() != null ? bankRes.getBank() : "")));

                Table table = new Table(2);
                table.addCell(new Cell().add(new Paragraph("Label")));
                table.addCell(new Cell().add(new Paragraph("Value")));

                List<ContentDTO> contentList = bankRes.getContent();
                if (contentList != null) {
                    for (ContentDTO content : contentList) {
                        table.addCell(new Cell().add(new Paragraph((content.getLabel() != null ? content.getLabel() : ""))));
                        table.addCell(new Cell().add(new Paragraph((content.getValue() != null ? content.getValue() : ""))));
                    }
                }

                document.add(table);
            }
        }

        document.close();
        return baos.toByteArray();
    }

}
