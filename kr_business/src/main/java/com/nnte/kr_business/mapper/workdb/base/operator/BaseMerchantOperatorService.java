package com.nnte.kr_business.mapper.workdb.base.operator;

import com.nnte.kr_business.base.BaseService;
import org.springframework.stereotype.Component;

@Component
public class BaseMerchantOperatorService extends BaseService<BaseMerchantOperatorDao,BaseMerchantOperator> {
    public BaseMerchantOperatorService(){
        super(BaseMerchantOperatorDao.class);
    }
}

