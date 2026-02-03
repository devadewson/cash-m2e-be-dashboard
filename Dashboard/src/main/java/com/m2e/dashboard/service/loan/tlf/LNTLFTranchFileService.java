package com.m2e.dashboard.service.loan.tlf;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.dto.loan.tlf.response.LNTLFTrancheDTO;
import com.m2e.dashboard.service.FileService;
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
public class LNTLFTranchFileService implements FileService<List<LNTLFTrancheDTO>> {

    private static final Logger log = LoggerFactory.getLogger(LNTLFTranchFileService.class);

    @Override
    public byte[] generateCsv(List<LNTLFTrancheDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] headers = {"Tranch Number","Interest Rate(%)", "Interest Payment Frequency", "Drawdown Amount"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getTrancheNumber() != null ? dto.getTrancheNumber() : "-",
                            dto.getRate() != null ? dto.getRate() : "-",
                            dto.getPaymentFrequency() != null ? dto.getPaymentFrequency() : "-",
                            dto.getDrawdownAmount() != null && dto.getCurrency() != null ? dto.getCurrency().concat(" ").concat(dto.getDrawdownAmount()) : "-"
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
    public byte[] generatePdf(List<LNTLFTrancheDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<LNTLFTrancheDTO, String>> columnGetters = Arrays.asList(
                    LNTLFTrancheDTO::getTrancheNumber,
                    LNTLFTrancheDTO::getRate,
                    LNTLFTrancheDTO::getPaymentFrequency,
                    LNTLFTrancheDTO::getDrawdownAmount
            );
            float totalTableWidth = calculateTotalTableWidth(datas, columnGetters);
            PageSize pageSize = getOptimalPageSize(totalTableWidth);


            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, pageSize);

            float[] columnWidths = calculateColumnWidths(datas, columnGetters, pageSize.getWidth());
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Tranch Number")));
            table.addHeaderCell(new Cell().add(new Paragraph("Interest Rate(%)")));
            table.addHeaderCell(new Cell().add(new Paragraph("Interest Payment Frequency")));
            table.addHeaderCell(new Cell().add(new Paragraph("Drawdown Amount")));

            datas.forEach(row -> {
                table.addCell(new Cell().add(new Paragraph(row.getTrancheNumber() != null ? row.getTrancheNumber() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getRate() != null ? row.getRate() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getPaymentFrequency() != null ? row.getPaymentFrequency() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getDrawdownAmount() != null && row.getCurrency() != null ? row.getCurrency().concat(" ").concat(row.getDrawdownAmount()) : "-")));
            });

            Paragraph title = new Paragraph("Loan Term Loan Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold();
            document.add(title);
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            log.error("generatePdf: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<LNTLFTrancheDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-20s%-20s%-28s%-20s%n",
                "Tranch Number","Interest Rate(%)","Interest Payment Frequency","Drawdown Amount"));

        datas.forEach(row -> {
            String trancheNumber = row.getTrancheNumber() != null ? row.getTrancheNumber() : "-";
            String rate = row.getRate() != null ? row.getRate() : "-";
            String paymentFrequency = row.getPaymentFrequency() != null ? row.getPaymentFrequency() : "-";
            String drawdownAmount = row.getDrawdownAmount() != null && row.getCurrency() != null ? row.getCurrency().concat(" ").concat(row.getDrawdownAmount()) : "-";

            String formattedRow = String.format("%-20s%-20s%-28s%-20s",
                    truncateOrPad(trancheNumber, 20),
                    truncateOrPad(rate, 20),
                    truncateOrPad(paymentFrequency, 28),
                    truncateOrPad(drawdownAmount, 20));
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
