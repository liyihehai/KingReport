package com.nnte.kr_business.mapper.workdb.merchant.rec;

import com.nnte.framework.base.BaseService;
import org.springframework.stereotype.Component;

@Component
public class MerchantReportRecService extends BaseService<MerchantReportRecDao,MerchantReportRec> {
    public MerchantReportRecService(){
        super(MerchantReportRecDao.class);
    }
}

