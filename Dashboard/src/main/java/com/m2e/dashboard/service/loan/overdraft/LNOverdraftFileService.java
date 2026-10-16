//package com.m2e.dashboard.service.loan.overdraft;
//
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.element.Paragraph;
//import com.itextpdf.layout.element.Table;
//import com.m2e.dashboard.dto.loan.overdraft.response.LNOverdraftDTO;
//import com.m2e.dashboard.service.FileService;
//import com.opencsv.CSVWriter;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.stereotype.Service;
//
//import java.io.ByteArrayOutputStream;
//import java.io.StringWriter;
//import java.nio.charset.StandardCharsets;
//import java.util.List;
//import java.util.stream.Collectors;
//
//@Service
//public class LNOverdraftFileService implements FileService<List<LNOverdraftDTO>> {
//
//    private static final Logger log = LoggerFactory.getLogger(LNOverdraftFileService.class);
//
//    @Override
//    public byte[] generateCsv(List<LNOverdraftDTO> datas) {
//        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
//            // TODO : modify this logic based on real report format
//            String[] headers = {"Account Name", "Account Number", "Overdraft Type", "Overdraft Limit", "Utilised Amount"};
//            csvWriter.writeNext(headers);
//            List<String[]> dataRows = datas.stream()
//                    .map(dto -> new String[]{
//                            dto.getAcctName() != null ? dto.getAcctName() : "-",
//                            dto.getAcctNumber() != null && dto.getAccountCurrency() != null ? dto.getAcctNumber().concat(" - ").concat(dto.getAccountCurrency()) : "-",
//                            dto.getOverdraftType() != null ? dto.getOverdraftType() : "",
//                            dto.getOverdraftLimit() != null ? dto.getOverdraftLimit() : "-",
//                            dto.getUtilisedAmount() != null ? dto.getUtilisedAmount() : "-"
//                    })
//                    .collect(Collectors.toList());
//            csvWriter.writeAll(dataRows);
//            return writer.toString().getBytes(StandardCharsets.UTF_8);
//        } catch (Exception e) {
//            log.error("generateCsv: {} -> {} ", e.getMessage(), e);
//            return new byte[0];
//        }
//    }
//
//    @Override
//    public byte[] generatePdf(List<LNOverdraftDTO> datas) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        try {
//            PdfWriter writer = new PdfWriter(baos);
//            PdfDocument pdf = new PdfDocument(writer);
//            Document document = new Document(pdf);
//            // TODO : modify this logic based on real report format
//            Table table = new Table(5);
//            table.addHeaderCell("Account Name");
//            table.addHeaderCell("Account Number");
//            table.addHeaderCell("Overdraft Type");
//            table.addHeaderCell("Overdraft Limit");
//            table.addHeaderCell("Utilised Amount");
//
//            datas.stream().forEach(row -> {
//                table.addCell(row.getAcctName() != null ? row.getAcctName() : "-");
//                table.addCell(row.getAcctNumber() != null && row.getAccountCurrency() != null ? row.getAcctNumber().concat(" - ").concat(row.getAccountCurrency()) : "-");
//                table.addCell(row.getOverdraftType() != null ? row.getOverdraftType() : "-");
//                table.addCell(row.getOverdraftLimit() != null ? row.getOverdraftLimit() : "-");
//                table.addCell(row.getUtilisedAmount() != null ? row.getUtilisedAmount() : "-");
//            });
//
//            document.add(new Paragraph("Loan Overdrafts Report"));
//            document.add(table);
//            document.close();
//
//            return baos.toByteArray();
//        } catch (Exception e) {
//            log.error("generatePdf: {} -> {} ", e.getMessage(), e);
//            return new byte[0];
//        }
//    }
//
//    @Override
//    public byte[] generateTxt(List<LNOverdraftDTO> datas) {
//        StringBuilder sb = new StringBuilder();
//        sb.append(String.format("%-140s%-20s%-20s%-20s%-20s\n",
//                "Account Name", "Account Number",
//                "Overdraft Type", "Overdraft Limit", "Utilised Amount"));
//
//        datas.forEach(row -> {
//            String acctName = row.getAcctName() != null ? row.getAcctName() : "-";
//            String acctNumber = row.getAcctNumber() != null && row.getAccountCurrency() != null ? row.getAcctNumber().concat(" - ").concat(row.getAccountCurrency()) : "-";
//            String overdraftType = row.getOverdraftType() != null ? row.getOverdraftType() : "-";
//            String overdraftLimit = row.getOverdraftLimit() != null ? row.getOverdraftLimit() : "-";
//            String utilisedAmount = row.getUtilisedAmount() != null ? row.getUtilisedAmount() : "-";
//
//            String formattedRow = String.format("%-140s%-20s%-20s%-20s%-20s",
//                    truncateOrPad(acctName, 140),
//                    truncateOrPad(acctNumber, 20),
//                    truncateOrPad(overdraftType, 20),
//                    truncateOrPad(overdraftLimit, 20),
//                    truncateOrPad(utilisedAmount, 20));
//            sb.append(formattedRow).append("\n");
//        });
//
//        return sb.toString().getBytes(StandardCharsets.UTF_8);
//    }
//
//    private String truncateOrPad(String input, int length) {
//        if (input.length() > length) {
//            return input.substring(0, length);
//        } else {
//            return String.format("%-" + length + "s", input);
//        }
//    }
//}
