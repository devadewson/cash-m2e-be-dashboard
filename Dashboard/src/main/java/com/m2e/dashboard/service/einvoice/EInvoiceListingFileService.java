package com.m2e.dashboard.service.einvoice;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.service.FileService;
import com.m2e.dashboard.sp.einvoice.EinvoiceListingSpDTO;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
public class EInvoiceListingFileService implements FileService<List<EinvoiceListingSpDTO>> {

    private static final Logger logger = LoggerFactory.getLogger(EInvoiceListingFileService.class);

    @Override
    public byte[] generateCsv(List<EinvoiceListingSpDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] headers = {"Company Name", "Business Registration No." ,"Status"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getCorpName() != null ? dto.getCorpName() : "",
                            dto.getBusinessReqNumber() != null ? dto.getBusinessReqNumber() : "",
                            dto.getStatus() != null ? dto.getStatus() : ""
                    })
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Error E-invoice Listing generateCsv: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<EinvoiceListingSpDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<EinvoiceListingSpDTO, String>> columnGetters = Arrays.asList(
                    EinvoiceListingSpDTO::getCorpName,
                    EinvoiceListingSpDTO::getBusinessReqNumber,
                    EinvoiceListingSpDTO::getStatus
            );
            float totalTableWidth = calculateTotalTableWidth(datas, columnGetters);
            PageSize pageSize = getOptimalPageSize(totalTableWidth);

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            float[] columnWidths = calculateColumnWidths(datas, columnGetters, pageSize.getWidth());
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Company Name")));
            table.addHeaderCell(new Cell().add(new Paragraph("Business Registration No.")));
            table.addHeaderCell(new Cell().add(new Paragraph("Status")));

            datas.stream().forEach(row -> {
                table.addCell(new Cell().add(new Paragraph(row.getCorpName() != null ? row.getCorpName() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getBusinessReqNumber() != null ? row.getBusinessReqNumber() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getStatus() != null ? row.getStatus() : "")));
            });

            Paragraph title = new Paragraph("E-invoice Listing Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold();
            document.add(title);
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Error E-invoice Listing while generatePdf: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<EinvoiceListingSpDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-150s%-40s%-30s\n",
                "Company Name", "Business Registration No.",
                "Status"));

        datas.forEach(row -> {
            String corpName = row.getCorpName() != null ? row.getCorpName() : "";
            String regNum = row.getBusinessReqNumber() != null ? row.getBusinessReqNumber() : "";
            String status = row.getStatus() != null ? row.getStatus() : "";

            String formattedRow = String.format("%-150s%-40s%-30s",
                    truncateOrPad(corpName, 150),
                    truncateOrPad(regNum, 40),
                    truncateOrPad(status, 30));
            sb.append(formattedRow).append("\n");
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
