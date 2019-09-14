package com.nnte.kr_business.mapper.workdb.base.merchant;

import com.nnte.kr_business.base.BaseService;
import org.springframework.stereotype.Component;

@Component
public class BaseMerchantService extends BaseService<BaseMerchantDao,BaseMerchant> {
    public BaseMerchantService(){
        super(BaseMerchantDao.class);
    }
}

