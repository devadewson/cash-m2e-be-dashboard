package com.m2e.dashboard.mapper.af.bd;

import com.m2e.dashboard.config.UserAndCorp;
import com.m2e.dashboard.dto.af.request.bd.AFBDAcctDetailReqDTO;
import com.m2e.dashboard.sp.af.bd.request.AFBDAgreementDetailSpParam;
import com.m2e.dashboard.sp.af.bd.request.AFBDDetailSpParam;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AFBDSpParamMapper {
    AFBDDetailSpParam mapDetailSp(AFBDAcctDetailReqDTO request, UserAndCorp userAndCorp);

    AFBDAgreementDetailSpParam mapAgreementDetailSp(AFBDAcctDetailReqDTO request, UserAndCorp userAndCorp);
}
