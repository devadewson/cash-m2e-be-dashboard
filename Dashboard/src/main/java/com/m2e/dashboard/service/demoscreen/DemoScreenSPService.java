package com.m2e.dashboard.service.demoscreen;

import com.m2e.dashboard.sp.demoscreen.request.DemoScreenSpParam;
import com.m2e.dashboard.sp.demoscreen.response.DemoScreenSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.m2e.dashboard.enums.SPEnum.SP_DEMO_SCREEN_UPDATE;

@Service
public class DemoScreenSPService {
    private static final Logger logger = LoggerFactory.getLogger(DemoScreenSPService.class);

    @Autowired
    BaseSpService baseSpService;

    public DemoScreenSpDTO updateUserJourney(DemoScreenSpParam spParam) {
        BaseSpDto<DemoScreenSpParam, DemoScreenSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_DEMO_SCREEN_UPDATE.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(DemoScreenSpDTO.class);

        BaseSpDto<DemoScreenSpParam, DemoScreenSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null) {
            logger.error("Empty SP result for Block Discounting masterAcctListing");
            return new DemoScreenSpDTO(0);
        }

        logger.info("masterAcctListing spResultList: {}", resultBaseSpDto.getResults());

        return resultBaseSpDto.getResults().get(0);
    }
}
