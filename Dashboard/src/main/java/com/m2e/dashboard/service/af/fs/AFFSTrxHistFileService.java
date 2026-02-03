package com.m2e.dashboard.service.af.fs;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.properties.UnitValue;
import com.m2e.dashboard.dto.af.request.fs.download.AFFSTrxHistDownloadReqDTO;
import com.m2e.dashboard.dto.af.response.fs.AFFSTrxHistDTO;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.loan.tlf.request.download.LNTLFTrxHistoryDownloadReqDTO;
import com.m2e.dashboard.dto.loan.tlf.response.LNTLFTrxHistoryDTO;
import com.m2e.dashboard.enums.DownloadEnum;
import com.m2e.dashboard.enums.ReportFormat;
import com.m2e.dashboard.service.DualParameterFileService;
import com.opencsv.CSVWriter;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Function;

import static com.m2e.dashboard.util.PdfUtility.*;

@Service
@Slf4j
public class AFFSTrxHistFileService implements DualParameterFileService<List<AFFSTrxHistDTO>> {
    private static final Logger logger = LoggerFactory.getLogger(AFFSTrxHistFileService.class);

    public void generateTrxHistoryFile(DownloadDTO response, List<AFFSTrxHistDTO> data, AFFSTrxHistDownloadReqDTO request) {
        switch (request.getFormat()) {
            case CSV:
                response.setData(generateCsv(data, request.getHeaders()));
                response.setFileName(DownloadEnum.AF_FS_TRX_HISTORY_FILENAME.getDescription() + "_" + request.getProvider() + "_" + String.valueOf(System.currentTimeMillis()) + ".csv");
                break;
            case PDF:
                response.setData(generatePdf(data, request.getHeaders()));
                response.setFileName(DownloadEnum.AF_FS_TRX_HISTORY_FILENAME.getDescription() + "_" + request.getProvider() + "_" + String.valueOf(System.currentTimeMillis()) + ".pdf");
                break;
            case TXT:
                response.setData(generateTxt(data, request.getHeaders()));
                response.setFileName(DownloadEnum.AF_FS_TRX_HISTORY_FILENAME.getDescription() + "_" + request.getProvider() + "_" + String.valueOf(System.currentTimeMillis()) + ".txt");
                break;
            default:
                throw new IllegalArgumentException("Report format is undefined");
        }
    }

    private Map<String, Function<AFFSTrxHistDTO, String>> getHeaderMap() {
        Map<String, Function<AFFSTrxHistDTO, String>> headerMap = new HashMap<>();
        headerMap.put("Date", AFFSTrxHistDTO::getTrxDate);
        headerMap.put("Processing Time", AFFSTrxHistDTO::getProcessingTime);
        headerMap.put("Transaction Reference", AFFSTrxHistDTO::getTrxReference);
        headerMap.put("Transaction Description", AFFSTrxHistDTO::getTrxDesc);
        headerMap.put("Credit", AFFSTrxHistDTO::getCredit);
        headerMap.put("Debit", AFFSTrxHistDTO::getDebit);
        headerMap.put("Transaction Description 2", AFFSTrxHistDTO::getTrxDesc2);
        headerMap.put("Transaction Description 3", AFFSTrxHistDTO::getTrxDesc3);
        headerMap.put("Transaction Description 4", AFFSTrxHistDTO::getTrxDesc4);
        headerMap.put("Virtual Account", AFFSTrxHistDTO::getVirtualAcct);
        headerMap.put("Transaction Status", AFFSTrxHistDTO::getTrxStatus);
        headerMap.put("Transaction Type", AFFSTrxHistDTO::getTrxType);
        headerMap.put("Source Code", AFFSTrxHistDTO::getSrcCode);
        headerMap.put("Teller ID", AFFSTrxHistDTO::getTellerId);
        headerMap.put("Owner's Insitution", AFFSTrxHistDTO::getOwnerInstitution);
        headerMap.put("Transaction Code", AFFSTrxHistDTO::getTrxCode);
        headerMap.put("Aux Transaction Code", AFFSTrxHistDTO::getAuxiliaryTrxCode);
        headerMap.put("Expiry Date", AFFSTrxHistDTO::getExpiryDate);
        return headerMap;
    }

    @Override
    public byte[] generateCsv(List<AFFSTrxHistDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); CSVWriter csvWriter = new CSVWriter(writer)) {
            csvWriter.writeNext(headers);
            List<String[]> dataRows = datas.stream()
                    .map(dto -> Arrays.stream(headers)
                            .map(header -> {
                                Function<AFFSTrxHistDTO, String> function = this.getHeaderMap().get(header);
                                return function != null && function.apply(dto)!=null ? function.apply(dto) : "-";
                            })
                            .toArray(String[]::new))
                    .toList();
            csvWriter.writeAll(dataRows);
            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Error occurred when generating csv file", e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generatePdf(List<AFFSTrxHistDTO> datas, String[] headers) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            List<Function<AFFSTrxHistDTO, String>> columnGetters = Arrays.stream(headers)
                    .map(header -> this.getHeaderMap().get(header))
                    .toList();
            List<String> columns = new ArrayList<>(this.getHeaderMap().keySet());

            float totalTableWidth = calculateTotalHeadersTableWidth(columns, columnGetters);
            PageSize pageSize = getOptimalPageSize(totalTableWidth);

            PdfWriter writer = new PdfWriter(baos);
            PdfDocument pdf = new PdfDocument(writer);
            pdf.setDefaultPageSize(pageSize);
            Document document = new Document(pdf);

            float[] columnWidths = calculateHeadersColumnWidths(columns, columnGetters, pageSize.getWidth());
            Table table = new Table(UnitValue.createPercentArray(columnWidths));
            table.setWidth(UnitValue.createPercentValue(100));
            Arrays.stream(headers)
                    .forEach(header -> table.addHeaderCell(new Cell().add(new Paragraph(header))));
            datas.forEach(row -> Arrays.stream(headers).forEach(
                    header -> {
                        Function<AFFSTrxHistDTO, String> function = this.getHeaderMap().get(header);
                        String cellValue = function != null && function.apply(row) != null ? function.apply(row) : "-";
                        table.addCell(new Cell().add(new Paragraph(cellValue)));
                    }));
            document.add(new Paragraph("Floor Stocking MY Transaction History"));
            document.add(table);
            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            logger.error("Error occurred when generating PDF file", e);
            return new byte[0];
        }
    }

    @Override
    public byte[] generateTxt(List<AFFSTrxHistDTO> datas, String[] headers) {
        try (StringWriter writer = new StringWriter(); PrintWriter txtWriter = new PrintWriter(writer)) {
            txtWriter.println(String.join("\t", headers));

            datas.stream().forEach(dto -> {
                String[] rowData = Arrays.stream(headers)
                        .map(header -> {
                            Function<AFFSTrxHistDTO, String> function = this.getHeaderMap().get(header);
                            return function != null && function.apply(dto) != null ? function.apply(dto) : "-";
                        })
                        .toArray(String[]::new);
                txtWriter.println(String.join("\t", rowData));
            });

            return writer.toString().getBytes(StandardCharsets.UTF_8);
        } catch (Exception e) {
            logger.error("Error occurred when generating txt file", e);
            return new byte[0];
        }
    }
}
