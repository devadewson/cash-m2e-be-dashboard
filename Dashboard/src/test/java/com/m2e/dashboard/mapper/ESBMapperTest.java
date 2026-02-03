package com.m2e.dashboard.mapper;

import com.m2e.dashboard.dto.af.response.fs.AFFSAcctDetailDTO;
import com.m2e.dashboard.dto.af.response.hp.AFHPAcctDetailDTO;
import com.m2e.dashboard.dto.af.response.hp.AFHPSGAcctDetailDTO;
import com.m2e.dashboard.dto.deposit.ca.response.DepositAccountDetailDTO;
import com.m2e.dashboard.jms.af.fs.response.account.details.ESBAFFSAccountDTO;
import com.m2e.dashboard.jms.af.fs.response.account.details.ESBAFFSAccountDetailsDTO;
import com.m2e.dashboard.jms.af.hp.response.account.details.ESBAFHPAccountDTO;
import com.m2e.dashboard.jms.af.hp.response.account.details.ESBAFHPAccountDetailDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDTO;
import com.m2e.dashboard.jms.deposit.ca.response.account.details.ESBDepositCAAccountDetailDTO;
import com.m2e.dashboard.mapper.af.fs.ESBAFFSAcctDetailsMapper;
import com.m2e.dashboard.mapper.af.hp.ESBAFHPAcctDetailsMapper;
import com.m2e.dashboard.mapper.deposit.ca.ESBDepositCAAcctDetailsMapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@Slf4j
public class ESBMapperTest {

    private ESBDepositCAAcctDetailsMapper eSBDepositCAAcctDetailsMapper;

    private ESBAFHPAcctDetailsMapper esbAFHPAcctDetailsMapper;

    private ESBAFFSAcctDetailsMapper esbAFFSAcctDetailsMapper;

    @BeforeEach
    public void setUp() {
        eSBDepositCAAcctDetailsMapper = Mappers.getMapper(ESBDepositCAAcctDetailsMapper.class);
    }

    @BeforeEach
    public void setUp2() {
        esbAFHPAcctDetailsMapper = Mappers.getMapper(ESBAFHPAcctDetailsMapper.class);
    }

    @BeforeEach
    public void setUp3() {
        esbAFFSAcctDetailsMapper = Mappers.getMapper(ESBAFFSAcctDetailsMapper.class); }

    @Test
    public void mapCAAcctDetails() {
        ESBDepositCAAccountDTO esbDepositCAAccountDTO = generateDummyData();
        DepositAccountDetailDTO depositAccountDetailDTO = eSBDepositCAAcctDetailsMapper.mapToDepositAccountDetail(esbDepositCAAccountDTO);
        assertNotNull(depositAccountDetailDTO, "The mapping result should not be null");
        log.info("Data {} -> ", depositAccountDetailDTO);
    }

    @Test
    public void mapMFCAAcctDetails() {
        ESBDepositCAAccountDTO esbDepositCAAccountDTO = generateMFCADummyData();
        DepositAccountDetailDTO depositAccountDetailDTO = eSBDepositCAAcctDetailsMapper.mapToMFCADepositAccountDetail(esbDepositCAAccountDTO);
        assertNotNull(depositAccountDetailDTO, "The mapping result should not be null");
        log.info("Data {} -> ", depositAccountDetailDTO);
    }

    public static ESBDepositCAAccountDTO generateDummyData() {
        // Create mock details
        ESBDepositCAAccountDetailDTO detail1 = new ESBDepositCAAccountDetailDTO();
        detail1.setName("ledgerBalance");
        detail1.setValue("1000");

        ESBDepositCAAccountDetailDTO detail2 = new ESBDepositCAAccountDetailDTO();
        detail2.setName("availableBalance");
        detail2.setValue("800");

        ESBDepositCAAccountDetailDTO detail3 = new ESBDepositCAAccountDetailDTO();
        detail3.setName("customerOwnBalance");
        detail3.setValue("650");

        //mock missing value from ESB
        ESBDepositCAAccountDetailDTO detail4 = new ESBDepositCAAccountDetailDTO();
        detail4.setName("oneDayFloat");

        ESBDepositCAAccountDetailDTO detail5 = new ESBDepositCAAccountDetailDTO();
        detail5.setName("twoDayFloat");
        detail5.setValue("8900");

        List<ESBDepositCAAccountDetailDTO> details = Arrays.asList(detail1, detail2, detail3, detail4, detail5);

        ESBDepositCAAccountDTO mockDTO = new ESBDepositCAAccountDTO();
        mockDTO.setAccountNumber(generateRandomAccountNumber());
        mockDTO.setCurrency(generateRandomCurrency());
        mockDTO.setRegion("SG");
        mockDTO.setDetails(details);

        return mockDTO;
    }

    public static ESBDepositCAAccountDTO generateMFCADummyData() {
        // Create mock details
        ESBDepositCAAccountDetailDTO detail2 = new ESBDepositCAAccountDetailDTO();
        detail2.setName("availableBalance");
        detail2.setValue("800");

        //should map to ledgerBalance and actualBalance for MFCA
        ESBDepositCAAccountDetailDTO detail3 = new ESBDepositCAAccountDetailDTO();
        detail3.setName("currentBalance");
        detail3.setValue("650");

        //mock missing value from ESB
        ESBDepositCAAccountDetailDTO detail4 = new ESBDepositCAAccountDetailDTO();
        detail4.setName("oneDayFloat");

        ESBDepositCAAccountDetailDTO detail5 = new ESBDepositCAAccountDetailDTO();
        detail5.setName("twoDayFloat");
        detail5.setValue("8900");

        List<ESBDepositCAAccountDetailDTO> details = Arrays.asList(detail2, detail3, detail4, detail5);

        ESBDepositCAAccountDTO mockDTO = new ESBDepositCAAccountDTO();
        mockDTO.setAccountNumber(generateRandomAccountNumber());
        mockDTO.setCurrency(generateRandomCurrency());
        mockDTO.setRegion("SG");
        mockDTO.setDetails(details);

        return mockDTO;
    }

    private static String generateRandomAccountNumber() {
        return UUID.randomUUID().toString().replaceAll("-", "").substring(0, 10);
    }

    private static String generateRandomCurrency() {
        String[] currencies = {"MYR", "JPY", "SGD", "USD", "THB"};
        return currencies[new Random().nextInt(currencies.length)];
    }

    @Test
    public void mapHPAcctDetails() {
        ESBAFHPAccountDTO esbAfHpAccountDTO = generateDummyHPData();
        AFHPSGAcctDetailDTO esbHpAccountDetailDTO = esbAFHPAcctDetailsMapper.mapToAccountDetailSGResp(esbAfHpAccountDTO);
        assertNotNull(esbHpAccountDetailDTO, "The mapping result should not be null");
        log.info("Data {} -> ", esbHpAccountDetailDTO);
    }

    public static ESBAFHPAccountDTO generateDummyHPData() {
        // Create mock details
        ESBAFHPAccountDetailDTO detailList1 = new ESBAFHPAccountDetailDTO();
        detailList1.setName("interestRate");
        detailList1.setValue("8.000");

        List<ESBAFHPAccountDetailDTO> details = Arrays.asList(detailList1);

        ESBAFHPAccountDTO mockDTO = new ESBAFHPAccountDTO();
        mockDTO.setAccountNumber(generateRandomAccountNumber());
        mockDTO.setCurrency(generateRandomCurrency());
        mockDTO.setRegion("SG");
        mockDTO.setDetails(details);

        return mockDTO;
    }

    @Test
    public void mapFSAcctDetails() {
        ESBAFFSAccountDetailsDTO esbAfFsAccountDTO = generateDummyFSData();
        AFFSAcctDetailDTO esbFSAccountDetailDTO = esbAFFSAcctDetailsMapper.mapToAccountDetailSGResp(esbAfFsAccountDTO);
        assertNotNull(esbFSAccountDetailDTO, "The mapping result should not be null");
        log.info("Data {} -> ", esbFSAccountDetailDTO);
    }

    public static ESBAFFSAccountDetailsDTO generateDummyFSData() {
        // Create mock details
        ESBAFFSAccountDTO detailList1 = new ESBAFFSAccountDTO();
        detailList1.setName("interestRate");
        detailList1.setValue("8.000");

        List<ESBAFFSAccountDTO> details = Arrays.asList(detailList1);

        ESBAFFSAccountDetailsDTO mockDTO = new ESBAFFSAccountDetailsDTO();
        mockDTO.setAccountNumber(generateRandomAccountNumber());
        mockDTO.setCurrency(generateRandomCurrency());
        mockDTO.setRegion("SG");
        mockDTO.setDetails(details);

        return mockDTO;
    }
}
