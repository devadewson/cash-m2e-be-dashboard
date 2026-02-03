package com.m2e.dashboard.service.af.pbr;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.dto.af.response.pbr.AFPBRViewByFacilityDTO;
import com.m2e.dashboard.service.FileService;
import com.opencsv.CSVWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
public class AFPBRViewByFacilityFileService implements FileService<List<AFPBRViewByFacilityDTO>> {

    private static final Logger log = LoggerFactory.getLogger(AFPBRViewByFacilityFileService.class);

    private static final String[] headers = {"Master Account Name", "Master Account Number", "Currency", "No of Accounts", "Total Facility Limit", "Total Settlement Amount"};

    private static final Map<String, Function<AFPBRViewByFacilityDTO, String>> HEADER_MAP;
    static {
        Map<String, Function<AFPBRViewByFacilityDTO, String>> headerMap = new HashMap<>();
        headerMap.put(headers[0], AFPBRViewByFacilityDTO::getMasterAcctName);
        headerMap.put(headers[1], AFPBRViewByFacilityDTO::getMasterAcctNumber);
        headerMap.put(headers[2], AFPBRViewByFacilityDTO::getAccountCurrency);
        headerMap.put(headers[3], AFPBRViewByFacilityDTO::getNoOfAccount);
        headerMap.put(headers[4], AFPBRViewByFacilityDTO::getTotalFacilityLimit);
        headerMap.put(headers[5], AFPBRViewByFacilityDTO::getTotalSettlementAmount);
        HEADER_MAP = Collections.unmodifiableMap(headerMap);
    }

    private Map<String, Function<AFPBRViewByFacilityDTO, String>> getHeaderMap() {
        return HEADER_MAP;
    }

    @Override
    public byte[] generateCsv(List<AFPBRViewByFacilityDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> Arrays.stream(headers)
                            .map(header -> {
                                Function<AFPBRViewByFacilityDTO, String> function = this.getHeaderMap().get(header);
                                return function != null && function.apply(dto)!=null ? function.apply(dto) : "-";
                            })
                            .toArray(String[]::new))
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("generateCsv: {} -> {} ", e.getMessage(), e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<AFPBRViewByFacilityDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<AFPBRViewByFacilityDTO, String>> columnGetters = Arrays.asList(
                    AFPBRViewByFacilityDTO::getMasterAcctName,
                    AFPBRViewByFacilityDTO::getMasterAcctNumber,
                    AFPBRViewByFacilityDTO::getAccountCurrency,
                    AFPBRViewByFacilityDTO::getNoOfAccount,
                    AFPBRViewByFacilityDTO::getTotalFacilityLimit,
                    AFPBRViewByFacilityDTO::getTotalSettlementAmount
            );

            float totalTableWidth = calculateTotalTableWidth(datas, columnGetters);
            PageSize pageSize = getOptimalPageSize(totalTableWidth);

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf, pageSize);

            float[] columnWidths = calculateColumnWidths(datas, columnGetters, pageSize.getWidth());
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph(headers[0])));
            table.addHeaderCell(new Cell().add(new Paragraph(headers[1])));
            table.addHeaderCell(new Cell().add(new Paragraph(headers[2])));
            table.addHeaderCell(new Cell().add(new Paragraph(headers[3])));
            table.addHeaderCell(new Cell().add(new Paragraph(headers[4])));
            table.addHeaderCell(new Cell().add(new Paragraph(headers[5])));

            datas.forEach(row -> {
                table.addCell(new Cell().add(new Paragraph(row.getMasterAcctName() != null ? row.getMasterAcctName() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getMasterAcctNumber() != null ? row.getMasterAcctNumber() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getAccountCurrency() != null ? row.getAccountCurrency() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getNoOfAccount() != null ? row.getNoOfAccount() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getTotalFacilityLimit() != null ? row.getTotalFacilityLimit() : "-")));
                table.addCell(new Cell().add(new Paragraph(row.getTotalSettlementAmount() != null ? row.getTotalSettlementAmount() : "-")));
            });

            Paragraph title = new Paragraph("Auto Finance PBR View by Facility Report")
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
    public byte[] generateTxt(List<AFPBRViewByFacilityDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-140s%-25s%-10s%-15s%-25s%-25s\r\n",
                headers[0], headers[1],
                headers[2], headers[3], headers[4], headers[5]));

        datas.forEach(row -> {
            String acctName = row.getMasterAcctName() != null ? row.getMasterAcctName() : "-";
            String acctNumber = row.getMasterAcctNumber() != null ? row.getMasterAcctNumber() : "-";
            String currency = row.getAccountCurrency() != null ? row.getAccountCurrency() : "-";
            String noOfAcct = row.getNoOfAccount() != null ? row.getNoOfAccount() : "-";
            String totalFacility = row.getTotalFacilityLimit() != null ? row.getTotalFacilityLimit() : "-";
            String totalSettlement = row.getTotalSettlementAmount() != null ? row.getTotalSettlementAmount() : "-";

            String formattedRow = String.format("%-140s%-25s%-10s%-15s%-25s%-25s",
                    truncateOrPad(acctName, 140),
                    truncateOrPad(acctNumber, 25),
                    truncateOrPad(currency, 10),
                    truncateOrPad(noOfAcct, 15),
                    truncateOrPad(totalFacility, 25),
                    truncateOrPad(totalSettlement, 25));
            sb.append(formattedRow).append("\r\n");
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
