package com.nnte.kr_business.mapper.workdb.merchant.gendetail;

import com.nnte.framework.base.BaseService;
import org.springframework.stereotype.Component;

@Component
public class MerchantReportGendetailService extends BaseService<MerchantReportGendetailDao,MerchantReportGendetail> {
    public MerchantReportGendetailService(){
        super(MerchantReportGendetailDao.class);
    }
}

