package com.m2e.dashboard.service.esb;

import com.m2e.dashboard.jms.common.response.ESBDTO;

public interface ESBResponseProcessor<R> {
    R processResponse(ESBDTO result, Object request);
}
