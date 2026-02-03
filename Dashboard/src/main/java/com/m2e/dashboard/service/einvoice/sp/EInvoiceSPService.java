package com.m2e.dashboard.service.einvoice.sp;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.common.SearchCollectionResponse;
import com.m2e.dashboard.dto.einvoice.request.EInvoiceListingReqDTO;
import com.m2e.dashboard.sp.einvoice.EinvoiceListingSpDTO;
import com.m2e.utils.jdbc.model.dto.BaseSpDto;
import com.m2e.utils.jdbc.service.BaseSpService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.m2e.dashboard.enums.SPEnum.SP_E_INVOICE_CORP_LIST;
import com.m2e.dashboard.sp.einvoice.EinvoiceListSpParam;

@Service
public class EInvoiceSPService {
    private static final Logger logger = LoggerFactory.getLogger(EInvoiceSPService.class);

    private final BaseSpService baseSpService;

    @Autowired
    public EInvoiceSPService(BaseSpService baseSpService) {
        this.baseSpService = baseSpService;
    }

    public List<EinvoiceListingSpDTO> getEInvoiceListing(UserAndCorp userAndCorp) {
        EinvoiceListSpParam spParam = new EinvoiceListSpParam(userAndCorp.getUserId(), userAndCorp.getCorpId());

        BaseSpDto<EinvoiceListSpParam, EinvoiceListingSpDTO> baseSpDto = new BaseSpDto<>();
        baseSpDto.setSpId(SP_E_INVOICE_CORP_LIST.getValue());
        baseSpDto.setParameter(spParam);
        baseSpDto.setResultClass(EinvoiceListingSpDTO.class);

        BaseSpDto<EinvoiceListSpParam, EinvoiceListingSpDTO> resultBaseSpDto = baseSpService.execute(baseSpDto);
        if (resultBaseSpDto.getResults() == null) {
            logger.error("Empty SP result for getEInvoiceListing");
            return new ArrayList<>();
        }
        List<EinvoiceListingSpDTO> spResultList = Optional.ofNullable(resultBaseSpDto).map(BaseSpDto::getResults)
                .orElse(Collections.emptyList());
        logger.info("getEInvoiceListing spResultList: {}", spResultList);

        return spResultList;
    }
}
