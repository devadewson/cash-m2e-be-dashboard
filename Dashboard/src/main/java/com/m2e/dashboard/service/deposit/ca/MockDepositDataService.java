package com.m2e.dashboard.service.deposit.ca;

import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ContentDTO;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.deposit.ca.request.DepositAcctFloatReqDTO;
import com.m2e.dashboard.dto.deposit.ca.response.*;
import com.m2e.dashboard.jms.common.request.ESBAcctListingReqDTO;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MockDepositDataService {

    public ApiResponse<DepositCAGPIStatusDTO> mockGPIStatus() {
        List<ContentDTO> contentDTOList1 = new ArrayList<>();
        contentDTOList1.add(new ContentDTO("remitterBank", "Maybank Singapore 63 Old Klang Road 1, Singapore MBBSG00812"));
        contentDTOList1.add(new ContentDTO("acceptanceDateTime", "05 Apr 2024 10:00"));
        contentDTOList1.add(new ContentDTO("elapsedTime", "04 Days 09:00:00"));
        contentDTOList1.add(new ContentDTO("amountRemitted", "SGD 10,000.00"));
        contentDTOList1.add(new ContentDTO("charges", "SGD 10.00"));
        contentDTOList1.add(new ContentDTO("exchangeRate", "52"));
        DepositCAGPIBankDTO depositCAGPIBankRes1 = new DepositCAGPIBankDTO();
        depositCAGPIBankRes1.setBank("originatingBank");
        depositCAGPIBankRes1.setContent(contentDTOList1);

        List<ContentDTO> contentDTOList2 = new ArrayList<>();
        contentDTOList2.add(new ContentDTO("intermediaryBank", "Maybank Singapore Deluxe 63 Old Klang Road 1, Singapore MBBSG00812"));
        contentDTOList2.add(new ContentDTO("acceptanceDateTime", "05 Apr 2024 10:00"));
        contentDTOList2.add(new ContentDTO("elapsedTime", "04 Days 09:00:00"));
        contentDTOList2.add(new ContentDTO("releaseDateTime", "05 Apr 2024 10:00"));
        DepositCAGPIBankDTO depositCAGPIBankRes2 = new DepositCAGPIBankDTO();
        depositCAGPIBankRes2.setBank("intermediaryBank1");
        depositCAGPIBankRes2.setContent(contentDTOList2);


        List<ContentDTO> contentDTOList3 = new ArrayList<>();
        contentDTOList3.add(new ContentDTO("intermediaryBank", "Maybank SBR 63 Old Klang Road 1, Singapore MBBSG00812"));
        contentDTOList3.add(new ContentDTO("acceptanceDateTime", "05 Apr 2024 10:00"));
        contentDTOList3.add(new ContentDTO("elapsedTime", "04 Days 09:00:00"));
        contentDTOList3.add(new ContentDTO("releaseDateTime", "05 Apr 2024 10:00"));
        DepositCAGPIBankDTO depositCAGPIBankRes3 = new DepositCAGPIBankDTO();
        depositCAGPIBankRes3.setBank("intermediaryBank2");
        depositCAGPIBankRes3.setContent(contentDTOList3);

        List<ContentDTO> contentDTOList4 = new ArrayList<>();
        contentDTOList4.add(new ContentDTO("recipientBank", "Maybank MC 63 Old Klang Road 1, Singapore MBBSG00812"));
        contentDTOList4.add(new ContentDTO("acceptanceDateTime", "05 Apr 2024 10:00"));
        contentDTOList4.add(new ContentDTO("elapsedTime", "04 Days 09:00:00"));
        contentDTOList4.add(new ContentDTO("amountCredited", "SGD 10,000.00"));
        contentDTOList4.add(new ContentDTO("charges", "SGD 10.00"));
        contentDTOList4.add(new ContentDTO("exchangeRate", "52"));
        DepositCAGPIBankDTO depositCAGPIBankRes4 = new DepositCAGPIBankDTO();
        depositCAGPIBankRes4.setBank("recipientBank");
        depositCAGPIBankRes4.setContent(contentDTOList4);

        DepositCAGPIStatusDTO depositCAGPIStatusDTO = new DepositCAGPIStatusDTO();
        List<DepositCAGPIBankDTO> bankList = new ArrayList<>();
        bankList.add(depositCAGPIBankRes1);
        bankList.add(depositCAGPIBankRes2);
        bankList.add(depositCAGPIBankRes3);
        bankList.add(depositCAGPIBankRes4);
        depositCAGPIStatusDTO.setBankList(bankList);
        depositCAGPIStatusDTO.setUetrNumber("AC89887230123");
        depositCAGPIStatusDTO.setGpiReason("-");
        depositCAGPIStatusDTO.setGpiStatus("Completed");
        depositCAGPIStatusDTO.setTotalElapsedTime("04 Days 09:00:00");

        ApiResponse<DepositCAGPIStatusDTO> apiResponse = new ApiResponse<>();
        apiResponse.setMsgHeader(new ErrorResponse("0"));
        apiResponse.setMsgBody(depositCAGPIStatusDTO);

        return apiResponse;
    }

}
