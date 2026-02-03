package com.m2e.dashboard.service.deposit.ca;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.m2e.dashboard.projection.DepositCASwiftMT103Projection;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.sql.Blob;
import java.util.Optional;

@Service
public class DepositCAMT130FileService {
    @SneakyThrows
    public byte[] generatePdf(Optional<DepositCASwiftMT103Projection> depositCASwiftMT103Projection) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document document = new Document(pdfDoc);
        if (depositCASwiftMT103Projection.isPresent() && depositCASwiftMT103Projection.get().getFileContent() != null) {
            Blob blob = depositCASwiftMT103Projection.get().getFileContent();
            byte[] txtData = blob.getBytes(1, (int) blob.length());
            String textContent = new String(txtData);
            document.add(new Paragraph(textContent));
        }
        document.close();
        pdfDoc.close();
        return outputStream.toByteArray();
    }

}
