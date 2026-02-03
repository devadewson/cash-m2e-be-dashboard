package com.m2e.dashboard.service.deposit.ca;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.dto.deposit.ca.response.DepositCAEODBalanceDTO;
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
public class DepositCAEODBalanceFileService implements FileService<List<DepositCAEODBalanceDTO>> {

    private static final Logger logger = LoggerFactory.getLogger(DepositCAEODBalanceFileService.class);

    @Override
    public byte[] generateCsv(List<DepositCAEODBalanceDTO> datas) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            String[] headers = {"Date", "Closing Ledger Balance" ,"Closing Available Balance"};
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> new String[]{
                            dto.getCreatedDate() != null ? dto.getCreatedDate() : "",
                            dto.getCurrency() != null && dto.getClosingLedgerBalance() != null ? dto.getCurrency() + " " + dto.getClosingLedgerBalance() : "",
                            dto.getCurrency() != null && dto.getClosingAvailableBalance() != null ? dto.getCurrency() + " " + dto.getClosingAvailableBalance() : ""
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
    public byte[] generatePdf(List<DepositCAEODBalanceDTO> datas) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<DepositCAEODBalanceDTO, String>> columnGetters = Arrays.asList(
                    DepositCAEODBalanceDTO::getCreatedDate,
                    DepositCAEODBalanceDTO::getClosingLedgerBalance,
                    DepositCAEODBalanceDTO::getClosingLedgerBalance
            );
            float totalTableWidth = calculateTotalTableWidth(datas, columnGetters);
            PageSize pageSize = getOptimalPageSize(totalTableWidth);

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            Document document = new Document(pdf);

            float[] columnWidths = calculateColumnWidths(datas, columnGetters, pageSize.getWidth());
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));

            table.addHeaderCell(new Cell().add(new Paragraph("Date")));
            table.addHeaderCell(new Cell().add(new Paragraph("Closing Ledger Balance")));
            table.addHeaderCell(new Cell().add(new Paragraph("Closing Available Balance")));

            datas.stream().forEach(row -> {
                table.addCell(new Cell().add(new Paragraph(row.getCreatedDate() != null ? row.getCreatedDate() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getCurrency() != null && row.getClosingLedgerBalance() != null ? row.getCurrency() + " " + row.getClosingLedgerBalance() : "")));
                table.addCell(new Cell().add(new Paragraph(row.getCurrency() != null && row.getClosingAvailableBalance() != null ? row.getCurrency() + " " + row.getClosingAvailableBalance() : "")));
            });

            String dateTimeTitle = !datas.isEmpty() && datas.get(0) != null && datas.get(0).getCreatedDate() != null
                    ? datas.get(0).getCreatedDate().substring(3)
                    : "";

            Paragraph title = new Paragraph("Deposit Current Accounts EOD Balance Report " + dateTimeTitle)
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
    public byte[] generateTxt(List<DepositCAEODBalanceDTO> datas) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%-30s%-30s%-30s\n",
                "Date", "Closing Ledger Balance",
                "Closing Available Balance"));

        datas.forEach(row -> {
            String date = row.getCreatedDate() != null ? row.getCreatedDate() : "";
            String ledgerBalance = row.getCurrency() != null && row.getClosingLedgerBalance() != null ? row.getCurrency() + " " + row.getClosingLedgerBalance() : "";
            String availableBalance = row.getCurrency() != null && row.getClosingAvailableBalance() != null ? row.getCurrency() + " " + row.getClosingAvailableBalance() : "";

            String formattedRow = String.format("%-30s%-30s%-30s",
                    truncateOrPad(date, 30),
                    truncateOrPad(ledgerBalance, 30),
                    truncateOrPad(availableBalance, 30));
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
