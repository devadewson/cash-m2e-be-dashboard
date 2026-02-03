package com.m2e.dashboard.service.deposit.fd;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.dto.deposit.fd.response.DepositFDAcctCertificatesDTO;
import com.m2e.dashboard.dto.deposit.fd.response.DepositFDCertificateDetailDTO;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
public class DepositFDCertificateFileService implements FileService<List<DepositFDCertificateDetailDTO.FullData>> {

    private static final Logger logger = LoggerFactory.getLogger(DepositFDCertificateFileService.class);

    @Override
    public byte[] generateCsv(List<DepositFDCertificateDetailDTO.FullData> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            // TODO : modify this logic based on real report format
            String[] headers = {"Preferred Name", "Certificate Number", "Profit Rate", "Maturity Date", "Currency", "Principal Amount"};
            csvWriter.writeNext(headers);
            DecimalFormat df = new DecimalFormat("0.00");
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getPreferredName() != null ? dto.getPreferredName() : "",
                            dto.getCertificateNumber() != null ? dto.getCertificateNumber()  : "",
                            dto.getInterestRate() != null && !dto.getInterestRate().isEmpty() ? dto.getInterestRate() + "%" : "",
                            dto.getMaturityDate() != null ? dto.getMaturityDate() : "",
                            dto.getCurrency() != null ? dto.getCurrency() : "",
                            dto.getPrincipalAmount() != null ? df.format(Double.parseDouble(dto.getPrincipalAmount())) : ""
                    })
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("generateCsv: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<DepositFDCertificateDetailDTO.FullData> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<DepositFDCertificateDetailDTO.FullData, String>> columnGetters = Arrays.asList(
                    DepositFDCertificateDetailDTO.FullData::getPreferredName,
                    DepositFDCertificateDetailDTO.FullData::getCertificateNumber,
                    DepositFDCertificateDetailDTO.FullData::getInterestRate,
                    DepositFDCertificateDetailDTO.FullData::getMaturityDate,
                    DepositFDCertificateDetailDTO.FullData::getCurrency,
                    DepositFDCertificateDetailDTO.FullData::getPrincipalAmount
            );
            float totalTableWidth = calculateTotalTableWidth(datas, columnGetters);
            PageSize pageSize = getOptimalPageSize(totalTableWidth);

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, pageSize);

            float[] columnWidths = calculateColumnWidths(datas, columnGetters, pageSize.getWidth());
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));
            DecimalFormat df = new DecimalFormat("0.00");

            table.addHeaderCell(new Cell().add(new Paragraph("Preferred Name")));
            table.addHeaderCell(new Cell().add(new Paragraph("Certificate Number")));
            table.addHeaderCell(new Cell().add(new Paragraph("Profit Rate")));
            table.addHeaderCell(new Cell().add(new Paragraph("Maturity Date")));
            table.addHeaderCell(new Cell().add(new Paragraph("Currency")));
            table.addHeaderCell(new Cell().add(new Paragraph("Principal Amount")));

            datas.stream().forEach(row -> {
                table.addCell(new Cell().add(new Paragraph(row.getPreferredName() != null ? row.getPreferredName() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getCertificateNumber() != null ? row.getCertificateNumber() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getInterestRate() != null && !row.getInterestRate().isEmpty() ? row.getInterestRate() + "%" : "")));
                table.addCell(new Cell().add(new Paragraph(row.getMaturityDate() != null ? row.getMaturityDate() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getCurrency() != null ? row.getCurrency() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getPrincipalAmount() != null ? df.format(Double.parseDouble(row.getPrincipalAmount())) : "")));
            });

            Paragraph title = new Paragraph("Fixed Deposit Certificates Report")
                    .setTextAlignment(TextAlignment.CENTER)
                    .setFontSize(14)
                    .setBold();
            document.add(title);
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("generatePdf: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<DepositFDCertificateDetailDTO.FullData> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-50s%-20s%-20s%-20s%-20s%-20s\n",
                "Preferred Name", "Certificate Number", "Profit Rate",
                "Maturity Date", "Currency", "Principal Amount"));
        DecimalFormat df = new DecimalFormat("0.00");

        datas.forEach(row -> {
            String preferredName = row.getPreferredName() != null ? row.getPreferredName() : "";
            String certificateNumber = row.getCertificateNumber() != null ? row.getCertificateNumber() : "";
            String profitRate = row.getInterestRate() != null && !row.getInterestRate().isEmpty() ? row.getInterestRate() + "%" : "";
            String currency = row.getCurrency() != null ? row.getCurrency() : "";
            String maturityDate = row.getMaturityDate() != null ? row.getMaturityDate() : "";
            String principalAmount = row.getPrincipalAmount() != null ? df.format(Double.parseDouble(row.getPrincipalAmount())) : "";

            String formattedRow = String.format("%-50s%-20s%-20s%-20s%-20s%-20s",
                    truncateOrPad(preferredName, 50),
                    truncateOrPad(certificateNumber, 20),
                    truncateOrPad(profitRate, 10),
                    truncateOrPad(maturityDate, 20),
                    truncateOrPad(currency, 3),
                    truncateOrPad(df.format(Double.parseDouble(principalAmount)), 20));
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
