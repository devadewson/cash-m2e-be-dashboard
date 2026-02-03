package com.m2e.dashboard.service.loan.overdraft;

import com.m2e.dashboard.dto.loan.overdraft.response.*;
import com.m2e.dashboard.sp.loan.od.request.LNODAcctListingSpParam;
import com.m2e.dashboard.sp.loan.od.request.LNODAcctDetailSpParam;
import com.m2e.dashboard.sp.loan.od.request.LNODAcctStatsSpParam;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static com.m2e.dashboard.enums.SPEnum.*;

@Component
public class LNOverdraftSPService {
    private static final Logger logger = LoggerFactory.getLogger(LNOverdraftSPService.class);
    @Autowired private BaseSpService baseSpService;

    private Random random = new SecureRandom();

    public LNOverdraftAccountStatisticDTO accountStatisticsOD(LNODAcctStatsSpParam lnodAcctStatsSpParam){
        BaseSpDto<LNODAcctStatsSpParam, LNOverdraftAccountStatisticDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_LN_OD_ACCT_STATS.getValue());
        baseSpDto.setParameter(lnodAcctStatsSpParam);
        baseSpDto.setResultClass(LNOverdraftAccountStatisticDTO.class);
        return baseSpService.execute(baseSpDto).getResults().get(0);
    }

    public BaseSpDto<LNODAcctListingSpParam, LNOverdraftDTO> accountListingOD(LNODAcctListingSpParam lnodAcctListingSpParam){
        BaseSpDto<LNODAcctListingSpParam, LNOverdraftDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_LN_OD_ACCT_LIST.getValue());
        baseSpDto.setParameter(lnodAcctListingSpParam);
        baseSpDto.setPageSize(lnodAcctListingSpParam.getPageSize());
        baseSpDto.setPageNumber(lnodAcctListingSpParam.getPage());
        baseSpDto.setResultClass(LNOverdraftDTO.class);
        return baseSpService.execute(baseSpDto);
    }

    public LNOverdraftDetailDTO accountDetailIntradayOD(LNODAcctDetailSpParam lnodAcctDetailSpParam){
        LNOverdraftDetailDTO accountDetailDTO;
        BaseSpDto<LNODAcctDetailSpParam, LNOverdraftDetailDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_LN_OD_ACCT_DTL.getValue());
        baseSpDto.setParameter(lnodAcctDetailSpParam);
        baseSpDto.setResultClass(LNOverdraftDetailDTO.class);

        try{
            accountDetailDTO = baseSpService.execute(baseSpDto).getResults().get(0);
        } catch (IndexOutOfBoundsException  e) {
            accountDetailDTO = new LNOverdraftDetailDTO();
            logger.error("No result found for SP: {}", baseSpDto.getSpId());
        }

        return accountDetailDTO;
    }

    private static String formatRandomNumber(String input) {
        StringJoiner joiner = new StringJoiner(" ");
        for (int i = 0; i < input.length(); i += 4) {
            int end = Math.min(i + 4, input.length());
            joiner.add(input.substring(i, end));
        }
        return joiner.toString();
    }

    public String generateRandomNumber(int length) {
        StringBuilder sb = new StringBuilder(length);
        ThreadLocalRandom randomNumber = ThreadLocalRandom.current();
        sb.append(randomNumber.nextInt(1, 10));
        for (int i = 1; i < length; i++) {
            sb.append(randomNumber.nextInt(0, 10));
        }
        return sb.toString();
    }

    public String generateRandomBigDecimal(int upperLimit, int decimalPlaces) {
        int integerValue = random.nextInt(upperLimit);
        BigDecimal fractionalValue = BigDecimal.valueOf(random.nextDouble()).setScale(decimalPlaces, RoundingMode.HALF_UP);
        return BigDecimal.valueOf(integerValue).add(fractionalValue).toPlainString();
    }


    public List<LNOverdraftOmnibusItemDTO> detailLoanOverdraftOmnibusAccounts(Pageable pageable){
        return new ArrayList<>(Arrays.asList(
                LNOverdraftOmnibusItemDTO.builder().acctName("Loan1").acctNumber(formatRandomNumber(generateRandomNumber(12))).accountCurrency("MYR").utilisedAmount(generateRandomBigDecimal(1000000, 2)).build(),
                LNOverdraftOmnibusItemDTO.builder().acctName("Loan2").acctNumber(formatRandomNumber(generateRandomNumber(12))).accountCurrency("MYR").utilisedAmount(generateRandomBigDecimal(1000000, 2)).build(),
                LNOverdraftOmnibusItemDTO.builder().acctName("Loan3").acctNumber(formatRandomNumber(generateRandomNumber(12))).accountCurrency("MYR").utilisedAmount(generateRandomBigDecimal(1000000, 2)).build(),
                LNOverdraftOmnibusItemDTO.builder().acctName("Loan4").acctNumber(formatRandomNumber(generateRandomNumber(12))).accountCurrency("MYR").utilisedAmount(generateRandomBigDecimal(1000000, 2)).build(),
                LNOverdraftOmnibusItemDTO.builder().acctName("Loan5").acctNumber(formatRandomNumber(generateRandomNumber(12))).accountCurrency("MYR").utilisedAmount(generateRandomBigDecimal(1000000, 2)).build()
        ));
    }
}