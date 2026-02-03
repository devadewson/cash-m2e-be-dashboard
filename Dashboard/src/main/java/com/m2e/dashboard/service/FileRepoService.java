package com.m2e.dashboard.service;

import com.m2e.cts.CtsReportDownload;
import com.m2e.cts.CtsReportEntity;
import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.DownloadDTO;
import com.m2e.dashboard.dto.base.DownloadReq;
import com.m2e.dashboard.enums.FileStatusEnum;
import com.m2e.dashboard.enums.Product;
import com.m2e.dashboard.enums.ReportTransactionTypeEnum;
import com.m2e.dashboard.projection.DepositAcctDetailPageProjection;
import com.m2e.dashboard.repository.CorpRepository;
import com.m2e.dashboard.repository.CtsReportDownloadRepository;
import com.m2e.dashboard.repository.CtsReportRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

import static com.m2e.dashboard.enums.ReportCommonConstant.REPORT_GENERATION_PROGRESS_STAGE_4;

@Service
public class FileRepoService {

    @Autowired
    private CorpRepository corpRepository;

    @Autowired
    private CtsReportRepository ctsReportRepository;

    @Autowired
    private CtsReportDownloadRepository ctsReportDownloadRepository;

    public void insertFileRepo(DownloadReq downloadReq, UserAndCorp userAndCorp, DownloadDTO result, DepositAcctDetailPageProjection acctData,
                               String acctNumber, String acctCurrency, Product product, ReportTransactionTypeEnum reportTransactionType, int progress) {
        CtsReportEntity ctsReportEntity = new CtsReportEntity();
        ctsReportEntity.setAccountNo(acctNumber);
        ctsReportEntity.setCorpId(userAndCorp.getCorpId());
        ctsReportEntity.setCreatedBy(userAndCorp.getUserId());
        ctsReportEntity.setDtCreated(OffsetDateTime.now());
        ctsReportEntity.setTsCreated(Instant.now());
        ctsReportEntity.setFileName(result.getFileName());
        ctsReportEntity.setFileType(downloadReq.getFormat().name());
        ctsReportEntity.setSystemProvider(acctData.getProvider());
        ctsReportEntity.setCurrencyCode(acctCurrency);
        ctsReportEntity.setProductCode(product.getCode());
        ctsReportEntity.setSubProductCode(acctData.getAccountType());
        ctsReportEntity.setTransactionType(reportTransactionType.name());
        ctsReportEntity.setProgress(BigDecimal.valueOf(progress));
        ctsReportEntity.setSME(isSMEbyCorpId(userAndCorp.getCorpId()));
        ctsReportEntity.setMultiCurrency(isMultiCurrency(acctData));
        ctsReportEntity.setFileRepo(false);
        ctsReportEntity.setCifNumber(acctData.getCifNo());
        ctsReportEntity.setFromDate(getTodayDate());
        ctsReportEntity.setToDate(getTodayDate());

        if (result.getData() != null) {
            ctsReportEntity.setReportFile(result.getData());
            ctsReportEntity.setStatus(FileStatusEnum.COMPLETED.name());
            ctsReportEntity.setStatusMessage(FileStatusEnum.COMPLETED.getDescription());
            ctsReportEntity.setIsCompleted("Y");
        } else {
            ctsReportEntity.setStatus(FileStatusEnum.PROCESSING.name());
            ctsReportEntity.setStatusMessage(FileStatusEnum.PROCESSING.getDescription());
        }

        ctsReportRepository.saveAndFlush(ctsReportEntity);
        if (REPORT_GENERATION_PROGRESS_STAGE_4 != progress) {
            ctsReportRepository.delete(ctsReportEntity);
        } else {
            addDownloadHistory(ctsReportEntity);
        }
    }

    public boolean isSMEbyCorpId(String corpId) {
        String businessSegmentCode = corpRepository.getMtBusinessSegCodeByCorpId(corpId).orElse(null);
        if (StringUtils.hasText(businessSegmentCode)) {
            return "CFS".equalsIgnoreCase(businessSegmentCode);
        }
        return false;
    }

    public boolean isMultiCurrency(DepositAcctDetailPageProjection acctData) {
        String acctType = acctData.getAccountType();
        if (StringUtils.hasText(acctType)) {
            return "MFCA".equalsIgnoreCase(acctType);
        }
        return false;
    }

    public String getTodayDate() {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String formattedDate = today.format(formatter);
        return formattedDate;
    }

    public void addDownloadHistory(CtsReportEntity ctsReportEntity){
        CtsReportDownload download = new CtsReportDownload();
        download.setCtsReportEntity(ctsReportEntity);
        download.setCreatedDate(OffsetDateTime.now(ZoneOffset.UTC));
        download.setCreatedTimeStamp(Instant.now());
        ctsReportDownloadRepository.save(download);
    }
}
