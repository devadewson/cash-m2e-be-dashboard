package com.m2e.dashboard.service.inv;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.m2e.dashboard.dto.investment.response.INVUnitTrustFundSearchDTO;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class INVUnitTrustFundFileService implements FileService<List<INVUnitTrustFundSearchDTO>> {

    String fundName = "Fund Name";
    String averageCost = "Average Cost";
    String units = "Units";
    String lastClosingNav = "Last Closing NAV";
    String estMarketValue = "Estimated Market Value";
    @Override
    public byte[] generateCsv(List<INVUnitTrustFundSearchDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] headers = {fundName, averageCost, units, lastClosingNav, estMarketValue};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getFundName() != null ? dto.getFundName() : "",
                            dto.getInitialCost() != null ? dto.getInitialCost() : "",
                            dto.getUnits() != null ? dto.getUnits() : "",
                            dto.getLastClosingNav() != null ? dto.getLastClosingNav() : "",
                            dto.getTotalValue() != null ? dto.getCurrency().concat(" ").concat(dto.getTotalValue()) : ""
                    })
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<INVUnitTrustFundSearchDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            Table table = new Table(5);

            table.addHeaderCell(fundName);
            table.addHeaderCell(averageCost);
            table.addHeaderCell(units);
            table.addHeaderCell(lastClosingNav);
            table.addHeaderCell(estMarketValue);

            datas.forEach(row -> {
                table.addCell(row.getFundName()!= null ? row.getFundName() : "");
                table.addCell(row.getInitialCost() != null ? row.getInitialCost() : "");
                table.addCell(row.getUnits() != null ? row.getUnits() : "");
                table.addCell(row.getLastClosingNav() != null ? row.getLastClosingNav() : "");
                table.addCell(row.getTotalValue() != null ? row.getCurrency().concat(" ").concat(row.getTotalValue()) : "");
            });

            document.add(new Paragraph("Unit Trust Fund Report"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<INVUnitTrustFundSearchDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-100s%-30s%-20s%-20s%-20s%n",
                fundName, averageCost, units, lastClosingNav, estMarketValue));

        datas.forEach(row -> {
            String fundNames = row.getFundName() != null ? row.getFundName() : "";
            String initialCost = row.getInitialCost() != null ? row.getInitialCost() : "";
            String noOfUnits = row.getUnits() != null ? row.getUnits() : "";
            String lastCloseNav = row.getLastClosingNav() != null ? row.getLastClosingNav() : "";
            String totalValue = row.getTotalValue() != null ? row.getCurrency().concat(" ").concat(row.getTotalValue()) : "";

            String formattedRow = String.format("%-100s%-30s%-20s%-20s%-20s",
                    truncateOrPad(fundNames, 100),
                    truncateOrPad(initialCost, 30),
                    truncateOrPad(noOfUnits, 20),
                    truncateOrPad(lastCloseNav, 20),
                    truncateOrPad(totalValue, 20));
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
