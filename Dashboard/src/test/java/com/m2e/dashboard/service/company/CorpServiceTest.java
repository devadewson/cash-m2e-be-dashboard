package com.m2e.dashboard.service.company;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.base.OpenTagComp;
import com.m2e.dashboard.dto.base.OpenTagCorpPageDTO;
import com.m2e.dashboard.dto.base.OpenTagCorpPageReqDTO;
import com.m2e.dashboard.dto.common.ApiResponse;
import com.m2e.dashboard.dto.common.ErrorResponse;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.enums.SourcePageEnum;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;

@ExtendWith(MockitoExtension.class)
class CorpServiceTest {

    @Spy
    @InjectMocks
    private CorpService corpService;

    private UserAndCorp userAndCorp;
    private OpenTagCorpPageReqDTO req;

    @BeforeEach
    void setup() {
        userAndCorp = new UserAndCorp();
        userAndCorp.setCorpId("C123");

        req = new OpenTagCorpPageReqDTO();
        req.setProduct("LOAN");
        req.setPage(0);
        req.setPageSize(10);
    }

    OpenTagComp createOpenTagComp() {
        OpenTagComp openTagComp = new OpenTagComp();
        openTagComp.setCorpCifNo(UUID.randomUUID().toString());
        openTagComp.setNoOfAccounts(1);
        openTagComp.setIsSubscriberCompOnly(true);
        return openTagComp;
    }

    OpenTagCorpPageDTO wrapOpenTagComp(OpenTagComp openTagComp) {
        SearchCollectionResponse<OpenTagComp> searchCollectionResponse = new SearchCollectionResponse<>();
        searchCollectionResponse.setData(List.of(openTagComp));
        searchCollectionResponse.setCurrentPage(1);
        searchCollectionResponse.setNextPage(-1);
        searchCollectionResponse.setTotalPages(1);
        searchCollectionResponse.setTotalRecordCount(1);
        return OpenTagCorpPageDTO.builder().data(searchCollectionResponse).build();
    }

    @Test
    void testLoanDefault_withAcctNmMaintenance_excludesRC() {
        // Arrange
        req.setProduct("LOAN");
        req.setSubProduct("UNKNOWN"); // triggers default branch
        req.setSource(SourcePageEnum.ACCT_NM_MAINTENANCE);

        OpenTagComp tlOpenTagComp = createOpenTagComp();
        OpenTagComp odOpenTagComp = createOpenTagComp();
        doReturn(new ApiResponse<>(new ErrorResponse("0"), wrapOpenTagComp(tlOpenTagComp)))
                .doReturn(new ApiResponse<>(new ErrorResponse("0"), wrapOpenTagComp(odOpenTagComp)))
                .when(corpService).getOpenTagCompanyListSp(any(), any(), any());

        // Act
        ApiResponse<OpenTagCorpPageDTO> response = corpService.getOpenTagCompanyList(userAndCorp, req);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getMsgHeader());
        assertEquals("0", response.getMsgHeader().getResponseCode());
        assertNotNull(response.getMsgBody());

        OpenTagCorpPageDTO combinedList = response.getMsgBody();
        assertEquals(2, combinedList.getData().getData().size());
        assertTrue(combinedList.getData().getData().contains(tlOpenTagComp));
        assertTrue(combinedList.getData().getData().contains(odOpenTagComp));
    }

    @Test
    void testLoanDefault_else_includesRC() {
        // Arrange
        req.setProduct("LOAN");
        req.setSubProduct("UNKNOWN"); // triggers default branch
        req.setSource(null); // not ACCT_NM_MAINTENANCE

        OpenTagComp tlOpenTagComp = createOpenTagComp();
        OpenTagComp odOpenTagComp = createOpenTagComp();
        OpenTagComp rcOpenTagComp = createOpenTagComp();
        doReturn(new ApiResponse<>(new ErrorResponse("0"), wrapOpenTagComp(tlOpenTagComp)))
                .doReturn(new ApiResponse<>(new ErrorResponse("0"), wrapOpenTagComp(odOpenTagComp)))
                .doReturn(new ApiResponse<>(new ErrorResponse("0"), wrapOpenTagComp(rcOpenTagComp)))
                .when(corpService).getOpenTagCompanyListSp(any(), any(), any());

        // Act
        ApiResponse<OpenTagCorpPageDTO> response = corpService.getOpenTagCompanyList(userAndCorp, req);

        // Assert
        assertNotNull(response);
        assertNotNull(response.getMsgHeader());
        assertEquals("0", response.getMsgHeader().getResponseCode());
        assertNotNull(response.getMsgBody());

        OpenTagCorpPageDTO combinedList = response.getMsgBody();
        assertEquals(3, combinedList.getData().getData().size());
        assertTrue(combinedList.getData().getData().contains(tlOpenTagComp));
        assertTrue(combinedList.getData().getData().contains(odOpenTagComp));
        assertTrue(combinedList.getData().getData().contains(rcOpenTagComp));
    }
}