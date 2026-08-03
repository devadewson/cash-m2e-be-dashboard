//package com.m2e.dashboard.service.trade.Refactor;
//
//
//import com.m2e.dashboard.model.DataMart.DatamartAgreement;
//import com.m2e.dashboard.repository.DatamartAgreementRepository;
//import jakarta.annotation.PostConstruct;
//import lombok.extern.slf4j.Slf4j;
//import net.schmizz.sshj.SSHClient;
//import net.schmizz.sshj.sftp.RemoteFile;
//import net.schmizz.sshj.sftp.SFTPClient;
//import net.schmizz.sshj.transport.verification.PromiscuousVerifier;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.io.*;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//import java.time.format.DateTimeFormatter;
//import java.util.ArrayList;
//import java.util.List;
//
//@Service
//@Slf4j
//public class SftpDataMartService {
//
//    @Value("${m2e.sftp.host}")
//    private String sftpHost;
//
//    @Value("${m2e.sftp.port}")
//    private int sftpPort;
//
//    @Value("${m2e.sftp.username}")
//    private String sftpUsername;
//
//    @Value("${m2e.sftp.password}")
//    private String sftpPassword;
//
//    @Value("${m2e.sftp.remote-directory}")
//    private String remoteDirectory;
//
//    @Value("${m2e.sftp.file-name}")
//    private String fileName;
//
//    @Value("${m2e.sftp.local-temp-directory}")
//    private String localTempDirectory;
//
//    @Autowired
//    private DatamartAgreementRepository repository;
//
//    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
//
//    @PostConstruct
//    public void init() {
//        File dir = new File(localTempDirectory);
//        if (!dir.exists()) {
//            dir.mkdirs();
//        }
//        log.info("SFTP Service initialized - Host: {}, Remote Directory: {}, File: {}",
//                sftpHost, remoteDirectory, fileName);
//    }
//
//    @Scheduled(fixedDelayString = "${m2e.sftp.polling-interval}")
//    @Transactional
//    public void downloadAndProcessFile() {
//        log.info("Starting SFTP file download with SSHJ");
//
//        SSHClient sshClient = new SSHClient();
//
//        try {
//            sshClient.addHostKeyVerifier(new PromiscuousVerifier());
//            sshClient.connect(sftpHost, sftpPort);
//            sshClient.authPassword(sftpUsername, sftpPassword);
//
//            log.info("Connected to SFTP: {}", sftpHost);
//
//            SFTPClient sftpClient = sshClient.newSFTPClient();
//
//            String remoteFile = remoteDirectory + "/" + fileName;
//            String localFile = localTempDirectory + File.separator + fileName;
//
//            new File(localTempDirectory).mkdirs();
//
//            try (RemoteFile rf = sftpClient.open(remoteFile);
//                 FileOutputStream fos = new FileOutputStream(localFile)) {
//
//                long fileSize = rf.length();
//                log.info("Remote file size: {} bytes", fileSize);
//
//                byte[] buffer = new byte[8192];
//                long offset = 0;
//                int bytesRead;
//
//                while (offset < fileSize) {
//                    bytesRead = rf.read(offset, buffer, 0, buffer.length);
//                    if (bytesRead == -1) break;
//                    fos.write(buffer, 0, bytesRead);
//                    offset += bytesRead;
//                }
//            }
//
//            sftpClient.close();
//
//            log.info("Downloaded: {}", localFile);
//            processFile(localFile);
//
//        } catch (Exception e) {
//            log.error("SFTP error: ", e);
//        } finally {
//            try {
//                if (sshClient.isConnected()) {
//                    sshClient.disconnect();
//                }
//            } catch (Exception e) {
//                log.error("Disconnect error: ", e);
//            }
//        }
//    }
//
//    private void processFile(String filePath) {
//        log.info("Processing file: {}", filePath);
//        List<DatamartAgreement> records = new ArrayList<>();
//        int successCount = 0;
//        int errorCount = 0;
//
//        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
//            String line;
//            boolean isHeader = true;
//
//            while ((line = reader.readLine()) != null) {
//                if (isHeader) {
//                    isHeader = false;
//                    continue;
//                }
//
//                if (line.trim().isEmpty()) {
//                    continue;
//                }
//
//                try {
//                    DatamartAgreement agreement = parseLine(line);
//                    if (agreement != null) {
//                        records.add(agreement);
//                        successCount++;
//                    }
//                } catch (Exception e) {
//                    errorCount++;
//                    log.error("Error parsing line {}: {}", successCount + errorCount, e.getMessage());
//                }
//
//                if (records.size() >= 1000) {
//                    repository.saveAll(records);
//                    records.clear();
//                }
//            }
//
//            if (!records.isEmpty()) {
//                repository.saveAll(records);
//            }
//
//            log.info("File processing completed. Success: {}, Errors: {}", successCount, errorCount);
//
//        } catch (IOException e) {
//            log.error("Error reading file: ", e);
//            throw new RuntimeException("Failed to read file: " + filePath, e);
//        }
//    }
//
//    private DatamartAgreement parseLine(String line) {
//        String[] fields = line.split("\\|", -1);
//
//        DatamartAgreement agreement = new DatamartAgreement();
//
//        agreement.setBaseDt(fields[0].trim());
//        agreement.setAgreeId(fields[1].trim());
//        agreement.setBrCd(fields[2].trim());
//        agreement.setAgreeNo(fields[3].trim());
//        agreement.setIssuanceDate(parseDate(fields[4]));
//        agreement.setOrigExpiryDate(parseDate(fields[5]));
//        agreement.setExpiryDate(parseDate(fields[6]));
//        agreement.setOrigCurrCd(fields[7].trim());
//        agreement.setCurrCd(fields[8].trim());
//        agreement.setMeasureCtg(fields[9].trim());
//        agreement.setPrdCtg(fields[10].trim());
//        agreement.setCifNo(fields[11].trim());
//        agreement.setCustRelBnk(fields[12].trim());
//        agreement.setCustOwnSt(fields[13].trim());
//        agreement.setCustCntryCd(fields[14].trim());
//        agreement.setNominal(parseBigDecimal(fields[15]));
//        agreement.setPrevOsBal(parseBigDecimal(fields[16]));
//        agreement.setDebitAmt(parseBigDecimal(fields[17]));
//        agreement.setCreditAmt(parseBigDecimal(fields[18]));
//        agreement.setOtherAmt(parseBigDecimal(fields[19]));
//        agreement.setOsBal(parseBigDecimal(fields[20]));
//        agreement.setIdrOsBal(parseBigDecimal(fields[21]));
//        agreement.setMarginDeposit(parseBigDecimal(fields[22]));
//        agreement.setCkpnIndv(parseBigDecimal(fields[23]));
//        agreement.setCkpnCol(parseBigDecimal(fields[24]));
//        agreement.setIntAmt(parseBigDecimal(fields[25]));
//        agreement.setPpapGenUnuse(parseBigDecimal(fields[26]));
//        agreement.setPpapGenUnuseIdr(parseBigDecimal(fields[27]));
//        agreement.setPpapSpecUnuse(parseBigDecimal(fields[28]));
//        agreement.setPpapSpecUnuseIdr(parseBigDecimal(fields[29]));
//        agreement.setIntRt(parseBigDecimal(fields[30]));
//        agreement.setIntRtTp(fields[31].trim());
//        agreement.setOsPerCifno(parseBigDecimal(fields[32]));
//        agreement.setCoaCdAsset(fields[33].trim());
//        agreement.setCoaCdLiab(fields[34].trim());
//        agreement.setDrwNo(fields[35].trim());
//        agreement.setNoteNo(fields[36].trim());
//        agreement.setRemainDays(parseLong(fields[37]));
//        agreement.setNegoBnk(fields[38].trim());
//        agreement.setNegoBnkCntryCd(fields[39].trim());
//        agreement.setIssuBank(fields[40].trim());
//        agreement.setIssuBankCtryCd(fields[41].trim());
//        agreement.setAdvBnk(fields[42].trim());
//        agreement.setAdvBnkCntryCd(fields[43].trim());
//        agreement.setClsCommAcctId(fields[44].trim());
//        agreement.setLob(fields[45].trim());
//        agreement.setLastEvDt(parseDate(fields[46]));
//        agreement.setLastEvTm(fields[47].trim());
//        agreement.setC05MtrAcptRelI(fields[48].trim());
//        agreement.setC05RelsCd2(parseBigDecimal(fields[49]));
//        agreement.setGrp(fields[50].trim());
//        agreement.setNo(fields[51].trim());
//        agreement.setSeq(fields[52].trim());
//        agreement.setObligClass(fields[53].trim());
//        agreement.setTbfDesc21(fields[54].trim());
//        agreement.setTbfDescription23(fields[55].trim());
//        agreement.setC05LcTp(fields[56].trim());
//        agreement.setMarketSegment(fields[57].trim());
//        agreement.setCpyDt(parseDate(fields[58]));
//        agreement.setCpyTm(fields[59].trim());
//        agreement.setApplicBnkCif(fields[60].trim());
//        agreement.setApplicBnkSwift(fields[61].trim());
//        agreement.setIssuBankCif(fields[62].trim());
//        agreement.setNegoBnkCif(fields[63].trim());
//        agreement.setGlfDbCrInd(fields[64].trim());
//        agreement.setGlfAcctNo(fields[65].trim());
//
//        if (fields.length > 66) {
//            agreement.setGlfTransAmt(parseBigDecimal(fields[66]));
//        }
//
//        agreement.setProcessDate(LocalDateTime.now());
//
//        return agreement;
//    }
//
//    private LocalDate parseDate(String dateStr) {
//        if (dateStr == null || dateStr.trim().isEmpty()) {
//            return null;
//        }
//        try {
//            return LocalDate.parse(dateStr.trim(), DATE_FORMATTER);
//        } catch (Exception e) {
//            log.warn("Error parsing date: {}", dateStr);
//            return null;
//        }
//    }
//
//    private BigDecimal parseBigDecimal(String value) {
//        if (value == null || value.trim().isEmpty()) {
//            return BigDecimal.ZERO;
//        }
//        try {
//            return new BigDecimal(value.trim());
//        } catch (NumberFormatException e) {
//            log.warn("Error parsing BigDecimal: {}", value);
//            return BigDecimal.ZERO;
//        }
//    }
//
//    private Long parseLong(String value) {
//        if (value == null || value.trim().isEmpty()) {
//            return null;
//        }
//        try {
//            return Long.parseLong(value.trim());
//        } catch (NumberFormatException e) {
//            log.warn("Error parsing Long: {}", value);
//            return null;
//        }
//    }
//}
