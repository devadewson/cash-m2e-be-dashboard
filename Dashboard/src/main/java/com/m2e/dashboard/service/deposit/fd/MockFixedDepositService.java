package com.m2e.dashboard.service.deposit.fd;

import com.m2e.dashboard.dto.base.OpenTagComp;
import com.m2e.dashboard.dto.deposit.fd.request.DepositFDTrxHistoryReqDTO;
import com.m2e.dashboard.dto.deposit.fd.response.*;
import com.m2e.dashboard.sp.deposit.fd.response.DepositFDAcctSummarySpDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@Service
public class MockFixedDepositService {

    Random random = new SecureRandom();

    public DepositFDAccountDetailDTO mockFixedDepositAccountStats() {
        DepositFDAccountDetailDTO depositFDAccountDetailDTO = new DepositFDAccountDetailDTO();

        Instant lastUpdate = Instant.now();
        ZoneId zoneId = ZoneId.of("Asia/Kuala_Lumpur");
        ZonedDateTime zonedDateTime = lastUpdate.atZone(zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss (zzz)");
        String formattedDateTimeUTC = zonedDateTime.format(formatter);

        depositFDAccountDetailDTO.setAcctName("Floor Stocking");
        depositFDAccountDetailDTO.setCompanyName("Petronas Sdn Bhd");
        depositFDAccountDetailDTO.setAcctNumber("3648 2592 7391");
        depositFDAccountDetailDTO.setCurrency("MYR");
        depositFDAccountDetailDTO.setLastUpdatedDateTime(formattedDateTimeUTC);

        depositFDAccountDetailDTO.setProductName("ISLAMIC FIXED DEPOSIT (eIFD)");
        depositFDAccountDetailDTO.setPrincipalAmount("300,000.00");
        depositFDAccountDetailDTO.setNoOfCertificates("3");

        return depositFDAccountDetailDTO;
    }

    public Page<DepositFDAcctCertificatesDTO> mockFixedDepositAcctCertificates() {
        List<DepositFDAcctCertificatesDTO> depositFDAcctCertificatesDTOList = new ArrayList<>();
        for (int x=0; x<4; x++) {
            depositFDAcctCertificatesDTOList.add(genFixedDepositCertificate());
        }

        PageRequest pageRequest = PageRequest.of(0, depositFDAcctCertificatesDTOList.size());
        return new PageImpl<>(depositFDAcctCertificatesDTOList, pageRequest, depositFDAcctCertificatesDTOList.size());
    }

    public List<DepositFDAcctCertificatesDTO> mockFixedDepositAcctCertificatesList() {
        List<DepositFDAcctCertificatesDTO> depositFDAcctCertificatesDTOList = new ArrayList<>();
        for (int x=0; x<4; x++) {
            depositFDAcctCertificatesDTOList.add(genFixedDepositCertificate());
        }
        return depositFDAcctCertificatesDTOList;
    }

    private DepositFDAcctCertificatesDTO genFixedDepositCertificate() {
        DepositFDAcctCertificatesDTO depositFDAcctCertificatesDTO = new DepositFDAcctCertificatesDTO();

        depositFDAcctCertificatesDTO.setCertificateNumber("FD40MY" + random.nextInt(100));
        depositFDAcctCertificatesDTO.setProfitRate("3.8%");
        depositFDAcctCertificatesDTO.setMaturityDate("7 Feb 2023");
        depositFDAcctCertificatesDTO.setPrincipalAmount(String.valueOf(random.nextInt(Integer.MAX_VALUE)));
        depositFDAcctCertificatesDTO.setCurrency("MYR");

        return depositFDAcctCertificatesDTO;
    }

    public Page<DepositFDTrxHistoryDTO> mockFixedDepositTrxHistory(DepositFDTrxHistoryReqDTO request) {
        List<DepositFDTrxHistoryDTO> depositFDTrxHistoryDTOList = new ArrayList<>();
        for (int x=0; x<4; x++) {
            depositFDTrxHistoryDTOList.add(genFixedDepositTrxHistory(x));
        }

        depositFDTrxHistoryDTOList = depositFDTrxHistoryDTOList
                .stream()
                .filter(result -> !StringUtils.hasText(request.getSearchParam()) || result.getTrxReference().contains(request.getSearchParam()))
                .filter(result -> !StringUtils.hasText(request.getFilters().getAmtFrom()) || Integer.parseInt(result.getDebit()) >= Integer.parseInt(request.getFilters().getAmtFrom())
                        && Integer.parseInt(result.getDebit()) <= Integer.parseInt(request.getFilters().getAmtTo()))
                .toList();

        if (depositFDTrxHistoryDTOList.isEmpty()) {
            return Page.empty();  // Return an empty Page if no results
        }

        PageRequest pageRequest = PageRequest.of(0, depositFDTrxHistoryDTOList.size());

        return new PageImpl<>(depositFDTrxHistoryDTOList, pageRequest, depositFDTrxHistoryDTOList.size());
    }

    private DepositFDTrxHistoryDTO genFixedDepositTrxHistory(int x) {
        DepositFDTrxHistoryDTO depositFDTrxHistoryDTO = new DepositFDTrxHistoryDTO();

        Instant lastUpdate = Instant.now();
        ZoneId zoneId = ZoneId.of("GMT+8");
        ZonedDateTime zonedDateTime = lastUpdate.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        String formattedDateTime = zonedDateTime.format(formatter);

        int amtFrom = x * 10000 - 500;
        int amtTo = x * 10000;

        depositFDTrxHistoryDTO.setDate(formattedDateTime);
        depositFDTrxHistoryDTO.setTrxReference("MBBSE00" + x);
        depositFDTrxHistoryDTO.setTrxDesc("<Description " + x + ">");
        depositFDTrxHistoryDTO.setDebit(String.valueOf(amtFrom));
        depositFDTrxHistoryDTO.setCredit(String.valueOf(amtTo));
        depositFDTrxHistoryDTO.setProcessingTime(formattedDateTime);
        depositFDTrxHistoryDTO.setTrxDesc2("<Description2>");
        depositFDTrxHistoryDTO.setExpiryDate(formattedDateTime);
        depositFDTrxHistoryDTO.setCurrency("MYR");
        depositFDTrxHistoryDTO.setSourceCode("");
        depositFDTrxHistoryDTO.setTellerId("");
        depositFDTrxHistoryDTO.setBranch("");
        depositFDTrxHistoryDTO.setTrxCode("");
        depositFDTrxHistoryDTO.setBeginningBalance("");
        depositFDTrxHistoryDTO.setEndBalance("");

        return depositFDTrxHistoryDTO;
    }

    public DepositFDCertificateDetailDTO.FullData mockFixedDepositCertificateDetail() {
        DepositFDCertificateDetailDTO.FullData depositFDCertificateDetailDTO = new DepositFDCertificateDetailDTO.FullData();

        Instant lastUpdate = Instant.now();
        ZoneId zoneId = ZoneId.of("Asia/Kuala_Lumpur");
        ZonedDateTime zonedDateTime = lastUpdate.atZone(zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss (zzz)");
        String formattedDateTimeUTC = zonedDateTime.format(formatter);

        DateTimeFormatter formatterCountry = DateTimeFormatter.ofPattern("d MMM yyyy 'MY'", Locale.ENGLISH); // NOSONAR
        String formattedDateTimeCountry = zonedDateTime.format(formatterCountry);


        depositFDCertificateDetailDTO.setCertificateNumber("FD40MY001");
        depositFDCertificateDetailDTO.setPrincipalAmount("100,00.00");
        depositFDCertificateDetailDTO.setPreferredName("Yeoh Sue Ann");

        depositFDCertificateDetailDTO.setCurrency("MYR");
        depositFDCertificateDetailDTO.setPlacementDate(formattedDateTimeCountry);
        depositFDCertificateDetailDTO.setTenure("1 Month");
        depositFDCertificateDetailDTO.setInterestRate("3.8%");
        depositFDCertificateDetailDTO.setMaturityInterest("48,416.79");
        depositFDCertificateDetailDTO.setMaturityDate(formattedDateTimeUTC);
        depositFDCertificateDetailDTO.setInstructionOnMaturity("Auto Renewal");
        depositFDCertificateDetailDTO.setInterestPaymentMode("Add to Principal");
        depositFDCertificateDetailDTO.setInterestCreditNumber("-");

        depositFDCertificateDetailDTO.setUpfrontInterestRatePA("MYR 48,416.79");
        depositFDCertificateDetailDTO.setInterestOnInterestRatePA("MYR 48,416.79");
        depositFDCertificateDetailDTO.setTotalInterest("MYR 48,416.79");
        depositFDCertificateDetailDTO.setUpfrontInterest("MYR 48,416.79");
        depositFDCertificateDetailDTO.setInterestOnInterestRate("MYR 48,416.79");
        depositFDCertificateDetailDTO.setTotalInterestMaturity("MYR 48,416.79");

        return depositFDCertificateDetailDTO;
    }

    private OpenTagComp genOpenTagComp() {
        OpenTagComp openTagComp = new OpenTagComp();

        openTagComp.setCorpId(String.valueOf(random.nextInt(Integer.MAX_VALUE)));
        openTagComp.setCompanyName("Sun " + random.nextInt(10) + " Shine Child Sdn Bhd.");
        openTagComp.setProvider("(MY)");
        openTagComp.setNoOfAccounts(10);

        return openTagComp;
    }

    private DepositFDAcctSummarySpDTO genAccountSummaries() {
        DepositFDAcctSummarySpDTO depositFDAcctSummarySpDTO = new DepositFDAcctSummarySpDTO();

        depositFDAcctSummarySpDTO.setAcctName("ISLAMIC FIXED DEPOSIT (eIFD) " + random.nextInt(10));
        depositFDAcctSummarySpDTO.setAcctNumber(String.valueOf(random.nextInt(Integer.MAX_VALUE)));
        depositFDAcctSummarySpDTO.setNoOfCertificates(String.valueOf(random.nextInt(50)));
        depositFDAcctSummarySpDTO.setTotalPrincipalAmt(String.valueOf(random.nextInt(Integer.MAX_VALUE)));

        return depositFDAcctSummarySpDTO;
    }

    public List<DepositFDAcctSummarySpDTO> mockAccountSummariesList() {
        List<DepositFDAcctSummarySpDTO> depositFDAcctSummarSpDTOList = new ArrayList<>();
        for (int x=0; x<10; x++) {
            depositFDAcctSummarSpDTOList.add(genAccountSummaries());
        }
        return depositFDAcctSummarSpDTOList;
    }
}
