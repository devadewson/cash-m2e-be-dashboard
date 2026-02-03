package com.m2e.dashboard.service.af.fs;

import com.m2e.dashboard.dto.af.request.fs.AFFSTrxHistReqDTO;
import com.m2e.dashboard.dto.af.response.fs.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class AFFSDataGenerator {
    public AFFSAcctStatViewAcctDTO mockFloorStockingAccountStatsViewAcctOverview() {
        AFFSAcctStatViewAcctDTO affsAcctStatViewAcctDTO = new AFFSAcctStatViewAcctDTO();

        Instant lastUpdate = Instant.now();
        ZoneId zoneId = ZoneId.of("Asia/Kuala_Lumpur");
        ZonedDateTime zonedDateTime = lastUpdate.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss (zzz)");
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd MMM yyyy");
        String formattedDateTimeUTC = zonedDateTime.format(formatter);
        String formattedDate = zonedDateTime.format(formatter1);

        affsAcctStatViewAcctDTO.setLastUpdatedDateTime(formattedDateTimeUTC);
        affsAcctStatViewAcctDTO.setLastUpdatedDate(formattedDate);
        affsAcctStatViewAcctDTO.setNumberOfAcct("10");
        affsAcctStatViewAcctDTO.setConsolidatedFinancedAmt("144080206.20");
        affsAcctStatViewAcctDTO.setConsolidatedOutstandingBalance("144080206.20");
        return affsAcctStatViewAcctDTO;
    }

    public AFFSAcctStatsViewFacilityDTO mockFloorStockingAccountStatsViewFacilityOverview() {
        AFFSAcctStatsViewFacilityDTO affsAcctStatsViewFacilityDTO = new AFFSAcctStatsViewFacilityDTO();

        Instant lastUpdate = Instant.now();
        ZoneId zoneId = ZoneId.of("Asia/Kuala_Lumpur");
        ZonedDateTime zonedDateTime = lastUpdate.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss (zzz)");
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("dd MMM yyyy");
        String formattedDateTimeUTC = zonedDateTime.format(formatter);
        String formattedDateTime = zonedDateTime.format(formatter1);

        affsAcctStatsViewFacilityDTO.setLastUpdatedDateTime(formattedDateTimeUTC);
        affsAcctStatsViewFacilityDTO.setLastUpdatedDate(formattedDateTime);
        affsAcctStatsViewFacilityDTO.setNumberOfAcct("10");
        return affsAcctStatsViewFacilityDTO;
    }

    public AFFSSGAcctDetailDTO mockFloorStockingSGAcctDetail() {
        AFFSSGAcctDetailDTO floorStockingSGAcctDetailRes = new AFFSSGAcctDetailDTO();

        Instant lastUpdate = Instant.now();
        ZoneId zoneId = ZoneId.of("Asia/Kuala_Lumpur");
        ZonedDateTime zonedDateTime = lastUpdate.atZone(zoneId);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy, HH:mm:ss (zzz)");
        String formattedDateTimeUTC = zonedDateTime.format(formatter);

        floorStockingSGAcctDetailRes.setAcctName("Stock EX 006");
        floorStockingSGAcctDetailRes.setAcctNumber("3648 2592 7391");
        floorStockingSGAcctDetailRes.setCurrency("SGD");
        floorStockingSGAcctDetailRes.setLastUpdatedDateTime(formattedDateTimeUTC);

        floorStockingSGAcctDetailRes.setOutstandingBalance("111000.00");

        floorStockingSGAcctDetailRes.setInterestAmt("234.00");
        floorStockingSGAcctDetailRes.setInterestRate("6.5%");
        floorStockingSGAcctDetailRes.setNextPaymentDate("23 Jul 2021");
        floorStockingSGAcctDetailRes.setPaymentType("Single Pay");
        floorStockingSGAcctDetailRes.setCollateralId("SAB 1234 C");
        floorStockingSGAcctDetailRes.setCollateralInformation("SAB 1234 C");
        floorStockingSGAcctDetailRes.setChassisNumber("1MRMZ23DXCU456789");
        floorStockingSGAcctDetailRes.setEngineNumber("12WVC10000");
        floorStockingSGAcctDetailRes.setLastPaymentDate("23 Jul 2021");
        floorStockingSGAcctDetailRes.setLateInterestOverdue("0.00");
        floorStockingSGAcctDetailRes.setCollateralDescription("This is a company vehicle started using in October 2020");

        return floorStockingSGAcctDetailRes;
    }

    private AFFSSubAcctListDTO genFloorStockingAcctListing(int x) {
        AFFSSubAcctListDTO afFSSubAcctListDTO = new AFFSSubAcctListDTO();

//        afFSSubAcctListDTO.setAcctName("Financial Cert " + x);
        afFSSubAcctListDTO.setCertNo("0172 8723 45" + x + x);
        afFSSubAcctListDTO.setCurrency("GBP");
        afFSSubAcctListDTO.setFinancedAmount("3562." + x + x);
        afFSSubAcctListDTO.setOutstandingBalance("3562." + x + x);

        return afFSSubAcctListDTO;
    }

    public Page<AFFSTrxHistDTO> mockFloodStockingSGTrxHistory(AFFSTrxHistReqDTO request) {
        List<AFFSTrxHistDTO> afFloorStockingSGTrxHistoryDTOList = new ArrayList<>();

        for (int x=0; x<10; x++) {
            afFloorStockingSGTrxHistoryDTOList.add(genFloorStockingSGTrxHistory(x));
        }

        PageRequest pageRequest = PageRequest.of(request.getPage(), request.getPageSize());
        return new PageImpl<>(afFloorStockingSGTrxHistoryDTOList, pageRequest, afFloorStockingSGTrxHistoryDTOList.size());
    }

    private AFFSTrxHistDTO genFloorStockingSGTrxHistory(int x) {
        AFFSTrxHistDTO afFSSGTrxHistoryDTO = new AFFSTrxHistDTO();

        Instant lastUpdate = Instant.now();
        ZoneId zoneId = ZoneId.of("GMT+8");
        ZonedDateTime zonedDateTime = lastUpdate.atZone(zoneId);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM yyyy HH:mm");
        String formattedDateTime = zonedDateTime.format(formatter);

        afFSSGTrxHistoryDTO.setTrxDate(formattedDateTime);
        afFSSGTrxHistoryDTO.setTrxReference(x + " months");
        afFSSGTrxHistoryDTO.setTrxDesc(x + "6423.00");
        afFSSGTrxHistoryDTO.setDebit("SGD");

        return afFSSGTrxHistoryDTO;
    }
}
